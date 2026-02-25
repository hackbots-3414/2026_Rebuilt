# Shooter Subsystem

## Overview

The Shooter subsystem controls three motors: two **flywheel motors** that spin a game piece up to speed, and one **hood motor** that adjusts the launch angle. Shot parameters (velocity and pitch) are supplied dynamically at runtime via `AimParams` objects, allowing the shooter to track a target continuously. The hood uses an absolute CANcoder for position feedback. The subsystem follows the same IO-layer pattern as the rest of the robot.

---

## Files

| File | Role |
|---|---|
| `Shooter.java` | Subsystem class — commands, triggers, unit conversions, and periodic loop |
| `ShooterConstants.java` | Hardware IDs, PID/motion magic tuning, speed limits, and hood geometry |
| `ShooterIO.java` | IO interface + logged inputs data class |
| `ShooterIOHardware.java` | Real-hardware implementation (3× TalonFX + CANcoder) |
| `ShooterIOSim.java` | Simulation implementation (minimal state store) |

---

## ShooterConstants

**Flywheel Motors**
- Motor 1 CAN ID: `53`
- Motor 2 CAN ID: `54`
- Motor 2 follows Motor 1 via `Follower` (aligned)
- Neutral mode: Coast
- Inversion: Clockwise Positive
- Supply current limit: 40 A
- Stator current limit: 125 A

**Flywheel PID (Slot 0)**

| Gain | Value |
|---|---|
| kP | 8.0 |
| kI | 0 |
| kD | 0 |
| kS | 0 |
| kV | 0 |
| kA | 0.6 |

- Motion Magic acceleration: 30 rot/s²
- Wheel radius: 2 inches
- Max linear (projectile) speed: 9.0 m/s
- Max rotational speed: 100 rot/s
- Reverse velocity (unjam): 30 rot/s

**Hood Motor (`HoodConstants`)**
- Motor CAN ID: `56`
- CANcoder CAN ID: `57`
- Neutral mode: Brake
- Inversion: Counter-Clockwise Positive
- Feedback source: Remote CANcoder
- Sensor-to-mechanism ratio: 155/15 (~10.33:1)
- Supply current limit: 40 A
- Stator current limit: 125 A

**Hood PID (Slot 0)**

| Gain | Value |
|---|---|
| kP | 40.0 |
| kI | 0 |
| kD | 0 |
| kS | 0 |
| kV | 10 |
| kA | 0.2 |

- Motion Magic cruise velocity: 3.0 rot/s
- Motion Magic acceleration: 4 rot/s²
- Forward soft limit: 0.065 rotations
- Reverse soft limit: 0.0 rotations
- Zero offset (`kOffset`): 18.0°
- CANcoder magnet offset: 0.24755859375 rotations
- CANcoder discontinuity point: 0.7 rotations

---

## AimParams (external, `frc.robot.aiming`)

`AimParams` is the data contract between the aiming system and the shooter. The shooter consumes it via a `Supplier<AimParams>` so shot parameters can update every loop cycle.

| Field | Type | Description |
|---|---|---|
| `status` | `AimStatus` | `Unchecked`, `Impossible`, or `Possible` |
| `control` | `SpeedControl` | `ProjectileVelocity` (m/s) or `MechanismControl` |
| `pitch` | `Rotation2d` | Desired launch angle |
| `yaw` | `Rotation2d` | Desired heading of the shooter (field-relative) |
| `output` | `double` | Desired projectile speed (m/s) or mechanism input |
| `deltaPitch` | `Rotation2d` | Tolerated pitch error (default ±4°) |
| `deltaYaw` | `Rotation2d` | Tolerated yaw error (default ±2°) |
| `deltaOutput` | `double` | Tolerated velocity error (default ±0.35 m/s) |

---

## ShooterIO Interface

**Methods**
- `updateInputs(ShooterIOInputs inputs)` — refreshes all sensor readings
- `setVelocity(AngularVelocity velocity)` — closed-loop velocity on both flywheel motors
- `setAngle(Angle angle)` — closed-loop position on the hood motor

**`ShooterIOInputs` (logged fields)**

**Flywheel Motor 1**

| Field | Unit | Description |
|---|---|---|
| `shooter1MotorConnected` | boolean | Signal health check |
| `shooter1SupplyCurrent` | Amps | Battery-side current |
| `shooter1TorqueCurrent` | Amps | Torque current |
| `shooter1StatorCurrent` | Amps | Stator current |
| `shooter1Voltage` | Volts | Applied voltage |
| `shooter1Temperature` | Celsius | Motor temperature |
| `shooter1Velocity` | Rot/s | Motor angular velocity |

**Flywheel Motor 2** — same fields prefixed `shooter2*`

**Hood Motor**

| Field | Unit | Description |
|---|---|---|
| `hoodMotorConnected` | boolean | Signal health check |
| `hoodSupplyCurrent` | Amps | Battery-side current |
| `hoodTorqueCurrent` | Amps | Torque current |
| `hoodStatorCurrent` | Amps | Stator current |
| `hoodVoltage` | Volts | Applied voltage |
| `hoodTemperature` | Celsius | Motor temperature |
| `hoodVelocity` | Rot/s | Motor angular velocity |
| `hoodPosition` | Rotations | Motor position (from TalonFX) |
| `hoodCANcoderConnected` | boolean | CANcoder signal health check |
| `hoodCANcoderPosition` | Rotations | Absolute position from CANcoder |

All fields are registered with `OnboardLogger` under the `"Shooter"` key.

---

## ShooterIOHardware

Real-robot implementation using three CTRE TalonFX motors and a CANcoder.

**Flywheel Control**
- `MotionMagicVelocityTorqueCurrentFOC` — velocity closed-loop with torque-current FOC.
- Motor 2 is configured as a `Follower` of Motor 1 with `Aligned` alignment; only Motor 1 receives control requests.
- Change detection: a new control request is only sent when velocity differs from the last commanded value.

**Hood Control**
- `DynamicMotionMagicVoltage` — position closed-loop using voltage output with on-device Motion Magic config (acceleration=0 and jerk=0 in the request defers to the configured values).
- Uses the remote CANcoder as the feedback source.
- Change detection: a new control request is only sent when the angle differs from the last commanded value.

---

## ShooterIOSim

Minimal simulation — no physics model.

- Stores hood angle and shooter velocity as local state.
- `updateInputs` only populates `shooter1MotorConnected`, `shooter1Velocity`, `hoodMotorConnected`, and `hoodPosition`; all other fields remain at zero defaults.
- Both setters store state immediately with no motion profile.

---

## Shooter (Subsystem)

**Constructor**
- Accepts a `ShooterIO` instance (injected).
- Registers a `disabled()` trigger that resets flywheel velocity to zero when the robot is disabled, preventing it from commanding back to the last setpoint on re-enable.
- Logs `hoodReference` and `shooterReference` via `OnboardLogger` under `"Shooter"`.

**`periodic()`**
- Calls `io.updateInputs(inputs)` each loop cycle.

**Unit Conversion Helpers (private)**

| Method | Description |
|---|---|
| `projectileToShooterVelocity(double projectileVelocity)` | Scales m/s projectile speed linearly to rot/s flywheel speed using `kMaxRotationalSpeed / kMaxLinearSpeed` |
| `pitchToHoodAngle(Rotation2d pitch)` | Converts launch pitch to hood mechanism angle: `(90° − pitch) − kOffset` |

**Commands**

| Method | Behavior |
|---|---|
| `shoot(Supplier<AimParams>)` | Continuously tracks `AimParams` — updates flywheel velocity and hood angle every loop cycle. Does NOT stop the shooter when the command ends. |
| `reverse()` | Runs flywheels in reverse at 30 rot/s to clear jams; stops on end |

**Trigger**

`tracked(Supplier<AimParams>)` — returns `true` when **both** of the following are simultaneously true:
1. `|shooter1Velocity − targetVelocity| ≤ deltaOutput` (velocity within tolerance)
2. `|hoodPosition − targetAngle| ≤ deltaPitch` (hood angle within tolerance)

This is used externally to gate game piece release — the indexer should only fire once the shooter is confirmed to be on target.

---

## Shoot Flow

```
AimParams supplier (updated each loop)
         │
         ▼
Shooter.shoot(params)
         │
         ├─ projectileToShooterVelocity(output)  → io.setVelocity(...)
         │
         └─ pitchToHoodAngle(pitch)              → io.setAngle(...)

         (command does NOT stop flywheels on end)

Indexer fires when:
         tracked(params) == true
         (velocity OK && hood angle OK)
```
