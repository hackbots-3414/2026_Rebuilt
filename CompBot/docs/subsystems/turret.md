# Turret Subsystem

## Overview

The Turret subsystem controls a rotating platform that points the shooter toward a target. It drives a single motor using Motion Magic position control, with absolute position determined at startup via a **Chinese Remainder Theorem (CRT)** algorithm using two CANcoders on gears with different gear ratios. This allows the turret to determine its absolute position across more than one full rotation without a multi-turn encoder. The turret continuously tracks a target yaw from `AimParams` and uses a custom **closest-congruent** algorithm to find the shortest valid path within its allowed travel range (±0.5 rotations, enforced by both software limits and the `findCC` algorithm).

---

## Files

| File | Role |
|---|---|
| `Turret.java` | Subsystem class — tracking, homing, position math, and telemetry |
| `TurretConstants.java` | Hardware IDs, travel limits, CRT encoder config, and motor tuning |
| `TurretIO.java` | IO interface + logged inputs data class |
| `TurretIOHardware.java` | Real-hardware implementation (TalonFX + 2× CANcoder + CRT) |
| `TurretIOSim.java` | Simulation implementation (first-order lag model) |

---

## TurretConstants

**Hardware**
- Motor CAN ID: `48`
- Encoder 1 CAN ID: `49`
- Encoder 2 CAN ID: `50`
- Supply current limit: 100 A
- Neutral mode: Coast
- Inversion: Counter-Clockwise Positive
- Feedback source: Rotor sensor (internal encoder)
- Sensor-to-mechanism ratio: 30:1

**Position References**

| Name | Value | Meaning |
|---|---|---|
| `kHomePosition` | 0.0 revolutions | Turret home/stow position (same as forward) |
| `kForwards` | 0.0 revolutions | Position where turret points directly forward on robot |

**Travel Limits**

| Name | Value |
|---|---|
| `kMinAngle` | −0.5 rotations |
| `kMaxAngle` | +0.5 rotations |

Software limit switches enforce these bounds. There is no longer a separate restricted tracking range.

**At-position tolerance:** 1°

**Motion Magic Profile**

| Parameter | Value |
|---|---|
| Cruise velocity | 3.0 rot/s |
| Acceleration | 10 rot/s² |

**PID (Slot 0)**

| Gain | Value |
|---|---|
| kP | 35 |
| kI | 0 |
| kD | 0.1 |
| kS | 0.6 |
| kV | 2.5 |
| kA | 0 |

**3D Offset (robot-relative)**
- X: −0.11 m
- Y: +0.11 m
- Z: +0.512 m

**CRT Encoder Config**

| | Encoder 1 | Encoder 2 |
|---|---|---|
| CAN ID | 49 | 50 |
| Magnet offset | −0.10400390625 | −0.50634765625 |
| Gear ratio | 72/12 (6:1) | (72×25)/(12×27) (~5.56:1) |
| Sensor direction | Default | Clockwise Positive |

---

## TurretIO Interface

**Methods**
- `updateInputs(TurretIOInputs inputs)` — refreshes all sensor readings
- `setPosition(Angle position)` — commands a Motion Magic position setpoint
- `calibrate()` — reads both CANcoders and solves the CRT to set absolute motor position

**`TurretIOInputs` (logged fields)**

| Field | Unit | Description |
|---|---|---|
| `motorConnected` | boolean | Whether all motor signals are valid |
| `calibrated` | boolean | Whether CRT calibration has succeeded at least once |
| `voltage` | Volts | Applied motor voltage |
| `supplyCurrent` | Amps | Battery-side current |
| `statorCurrent` | Amps | Stator current |
| `torqueCurrent` | Amps | Torque current |
| `temperature` | Celsius | Motor temperature |
| `velocity` | Rev/s | Motor angular velocity |
| `position` | Revolutions | Current motor position |
| `reference` | Radians | Last commanded position setpoint |

All fields are registered with `OnboardLogger` under the `"Turret"` key.

---

## TurretIOHardware

Real-robot implementation using a CTRE TalonFX and two CANcoders.

**Control Mode**
- `DynamicMotionMagicTorqueCurrentFOC` — position control with torque-current FOC and a full jerk-limited motion profile (velocity, acceleration, jerk all configured).

**CRT Calibration**
- Uses the `EasyCRT` library (`yams.units`) with both CANcoder positions and their respective gear ratios.
- `calibrate()` calls `crt.getAngleOptional()` — if the solve succeeds, the motor's internal position register is overwritten with the absolute result. If it fails, an error is reported to DriverStation.
- `calibrated` is a latch — once it succeeds once, it stays `true` even if subsequent calls fail.
- Motor position is initialized to 0 on construction, then overwritten by the first successful `calibrate()` call (which happens in the constructor).

**Signal Registration**
- Motor voltage, supply current, temperature, velocity, and position plus both CANcoder absolute positions are registered with `StatusSignalUtil`.

---

## TurretIOSim

Simulation with a first-order lag model.

- Each loop cycle: `position = position × 0.8 + reference × 0.2` — approximates a sluggish approach to the setpoint.
- `calibrated` state is read from and written back to SmartDashboard under `"Turret/Successful Calibration?"`, allowing manual override during simulation.
- `calibrate()` is a no-op in sim.

---

## Turret (Subsystem)

**Constructor**
- Accepts a `TurretIO` instance (injected).
- Calls `io.calibrate()` immediately on construction.
- Publishes SmartDashboard buttons for `Home` and `Calibrate`.
- Binds `RobotModeTriggers.disabled().onTrue(calibrate)` — re-calibrates the turret automatically every time the robot is disabled.
- Logs `ready()` trigger state, `tracking` boolean, and `reference` via `OnboardLogger`.

**`periodic()`**
- Calls `io.updateInputs(inputs)`.
- Sets the `calibrationAlert` (error-level) if `inputs.calibrated` is false.

**Commands**

| Method | Behavior |
|---|---|
| `track(StateManager)` | Continuously computes target yaw relative to robot heading and commands position; restricted to tracking range unless `shootReady` is true |
| `home()` | Commands `kHomePosition` (0.5 rev), waits until `ready()` |
| `forwards()` | Commands `kForwards` (0.25 rev), waits until `ready()` |

**`track()` detail:**
- Computes `mechanismAngle = aimParams.yaw − robotHeading + kForwards`
- Calls `setPosition(angle, !state.shootReady.getAsBoolean())` — passes `true` for the `tracking` flag only while not ready to shoot
- Currently, the tracking flag parameter is accepted by `setPosition()` but both paths use the same full range `[kMinAngle, kMaxAngle]` — the restricted range behavior (originally ±0.5 rot) is no longer enforced

**Triggers**

| Method | Returns true when... |
|---|---|
| `ready()` | `\|position − reference\| ≤ 1°` |
| `tracked(Supplier<AimParams>)` | `\|position − reference\| ≤ deltaYaw` AND `tracking == true` |

**Utility Methods**
- `turretPose(Pose2d robotPose)` returns the turret's 3D field pose by applying `kOffset` to the robot's field pose.
- `turretCameraOffset()` returns the 3D transform from the robot's root frame to the turret-mounted camera, accounting for the current turret angle. Used by the vision localization system to compute camera pose in field space.

---

## `findCC` — Closest Congruent Algorithm

`findCC(position, reference, min, max)` solves the problem of a continuous-rotation mechanism that can wrap: given the current unwrapped position, find the closest congruent (same angle mod 1) value within `[min, max]`.

**Steps:**
1. Clamp `reference` into `[min, max]` by adding/subtracting 1.0 until it fits.
2. If `|reference − position| < 0.5`, the clamped value is already closest — return it.
3. Otherwise, walk in the direction that reduces error (add or subtract 1.0 per step), stopping if the candidate goes out of bounds or error starts increasing.
4. Returns the best candidate found, or the original position if the range is invalid (< 1.0 wide).

This ensures the turret always takes the shortest valid path to the target angle, even across the wrap-around point.

```
Example:
  position = 0.1 rot, reference = 0.9 rot, range [-0.75, 0.75]
  |0.9 - 0.1| = 0.8 > 0.5 → try 0.9 - 1.0 = -0.1
  |-0.1 - 0.1| = 0.2 < 0.5 → return -0.1  (rotate backward, not the long way around)
```
