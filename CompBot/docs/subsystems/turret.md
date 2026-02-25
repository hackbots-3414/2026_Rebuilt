# Turret Subsystem

## Overview

The Turret subsystem controls a rotating platform that points the shooter toward a target. It drives a single motor using Motion Magic position control, with absolute position determined at startup via a **Chinese Remainder Theorem (CRT)** algorithm using two CANcoders on gears with different gear ratios. This allows the turret to determine its absolute position across more than one full rotation without a multi-turn encoder. The turret continuously tracks a target yaw from `AimParams` and uses a custom **closest-congruent** algorithm to find the shortest valid path within its allowed travel range.

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
- Sensor-to-mechanism ratio: 38.46:1

**Position References**

| Name | Value | Meaning |
|---|---|---|
| `kHomePosition` | 0.5 revolutions | Turret home/stow position |
| `kForwards` | 0.25 revolutions | Position where turret points directly forward on robot |

**Travel Limits**

| Name | Value | Used When |
|---|---|---|
| `kMinAngle` | −0.75 rotations | Full range (shoot mode) |
| `kMaxAngle` | +0.75 rotations | Full range (shoot mode) |
| `kMinTrackingAngle` | −0.5 rotations | Restricted range (tracking mode) |
| `kMaxTrackingAngle` | +0.5 rotations | Restricted range (tracking mode) |

The tighter tracking range prevents the turret from reaching its physical limits while seeking, reserving the extra travel for the final shot when full range access is granted.

**At-position tolerance:** 1°

**Motion Magic Profile**

| Parameter | Value |
|---|---|
| Cruise velocity | 32 rot/s |
| Acceleration | 48 rot/s² |
| Jerk | 480 rot/s³ |

**PID (Slot 0)**

| Gain | Value |
|---|---|
| kP | 50 |
| kI | 0 |
| kD | 0 |
| kS | 0.125 |
| kV | 0 |
| kA | 0 |

**3D Offset (robot-relative)**
- X: −4.4 inches
- Y: +4.4 inches
- Z: +22.5 inches

**CRT Encoder Config**

| | Encoder 1 | Encoder 2 |
|---|---|---|
| CAN ID | 49 | 50 |
| Magnet offset | −0.352051 | −0.531006 |
| Gear ratio | 100/12 (~8.33:1) | (100×28)/(12×26) (~8.97:1) |
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
- Logs `ready()` trigger state and `tracking` boolean via `OnboardLogger`.

**`periodic()`**
- Calls `io.updateInputs(inputs)`.
- Publishes position in revolutions to SmartDashboard under `"Position"`.
- Sets the `calibrationAlert` (error-level) if `inputs.calibrated` is false.

**Commands**

| Method | Behavior |
|---|---|
| `track(StateManager)` | Continuously computes target yaw relative to robot heading and commands position; restricted to tracking range unless `shootReady` is true |
| `home()` | Commands `kHomePosition` (0.5 rev), waits until `ready()` |
| `forwards()` | Commands `kForwards` (0.25 rev), waits until `ready()` |

**`track()` detail:**
- Computes `relative = aimParams.yaw − robotHeading`
- Adds `kForwards` offset so the angle is in turret mechanism space
- Uses the **restricted tracking range** (±0.5 rot) while waiting; switches to **full range** (±0.75 rot) once `state.shootReady()` is true — this prevents the turret from swinging to an extreme while tracking, but allows full travel for the final shot

**Triggers**

| Method | Returns true when... |
|---|---|
| `ready()` | `\|position − reference\| ≤ 1°` |
| `tracked(Supplier<AimParams>)` | `\|position − reference\| ≤ deltaYaw` AND `tracking == true` |

**Telemetry**
- `telemetrize(StateManager)` pushes two `Pose2d` objects to the Field2d widget: `"turret"` (actual position) and `"turret-target"` (reference position), both expressed in field coordinates.
- `turretPose(Pose2d)` returns the turret's 3D pose by applying `kOffset` to the robot's field pose.

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
