# Intake Subsystem

## Overview

The Intake subsystem controls two independent mechanisms: a **roller motor** that spins to ingest or eject game pieces, and a **deploy motor** that rotates the intake arm between stowed, agitate, and deployed positions. A **CANrange** proximity sensor detects whether a game piece is present. The subsystem also exposes an `intaking()` trigger that reflects whether the intake is actively running.

---

## Files

| File | Role |
|---|---|
| `Intake.java` | Subsystem class — commands, triggers, and periodic loop |
| `IntakeConstants.java` | Hardware IDs, voltage setpoints, motor configs, and deploy presets |
| `IntakeIO.java` | IO interface + logged inputs data class |
| `IntakeIOHardware.java` | Real-hardware implementation (2× TalonFX + CANrange) |
| `IntakeIOSim.java` | Simulation implementation (minimal state store) |

---

## IntakeConstants

**Intake Roller Motor**
- CAN ID: `5`
- Neutral mode: Coast
- Inversion: Clockwise Positive
- Supply current limit: 80 A
- Stator current limit: 120 A

**Intake Voltage Setpoints**

| Name | Value | Purpose |
|---|---|---|
| `kIntakeVoltage` | +12.0 V | Spin rollers inward to ingest |
| `kEjectVoltage` | −8.0 V | Spin rollers outward to eject |

**CANrange Sensor**
- CAN ID: `25`
- FOV: 6.5° × 6.5°
- Proximity threshold: 0.1 m
- Minimum signal strength: 15,015
- Update mode: Short Range @ 100 Hz

**Jam Detection Thresholds**

| Name | Value |
|---|---|
| `kJamStatorThreshold` | 70 A stator current |
| `kJamVelocityThreshold` | 0.3 rot/s |

**Deploy Motor (`DeployConstants`)**
- CAN ID: `6`
- CANcoder CAN ID: `29`
- Neutral mode: Brake
- Inversion: Clockwise Positive
- Feedback source: Remote CANcoder (ID 29)
- Supply current limit: 40 A
- Stator current limit: 40 A
- PID gains (Slot 0): kP=50, kI=0, kD=1, kS=0, kV=0, kA=0, kG=−0.5
- Max velocity: 0.4 rot/s
- Max acceleration: 4 rot/s²
- At-position tolerance: 0.5 rotations

**Deploy Position Presets (`DeployPosition` enum)**

| Name | Value |
|---|---|
| `Stow` | 0.0 rotations |
| `Agitate` | 0.17 rotations |
| `Deployed` | 0.224 rotations |

---

## IntakeIO Interface

**Methods**
- `updateInputs(IntakeIOInputs inputs)` — refreshes all sensor readings
- `setIntakeVoltage(Voltage voltage)` — open-loop voltage on the roller motor
- `setDeployPosition(Angle position)` — closed-loop position on the deploy motor

**`IntakeIOInputs` (logged fields)**

**Intake Roller Motor**

| Field | Unit | Description |
|---|---|---|
| `intakeMotorConnected` | boolean | Whether all intake motor signals are valid |
| `intakeSupplyCurrent` | Amps | Battery-side current draw |
| `intakeTorqueCurrent` | Amps | Motor torque current |
| `intakeStatorCurrent` | Amps | Motor stator current |
| `intakeVoltage` | Volts | Applied motor voltage |
| `intakeVelocity` | Rad/s | Motor angular velocity |
| `intakeTemperature` | Celsius | Motor temperature |

**Deploy Motor**

| Field | Unit | Description |
|---|---|---|
| `deployMotorConnected` | boolean | Whether all deploy motor signals are valid |
| `deploySupplyCurrent` | Amps | Battery-side current draw |
| `deployTorqueCurrent` | Amps | Motor torque current |
| `deployStatorCurrent` | Amps | Motor stator current |
| `deployVoltage` | Volts | Applied motor voltage |
| `deployVelocity` | Rad/s | Motor angular velocity |
| `deployTemperature` | Celsius | Motor temperature |
| `deployPosition` | Rotations | Motor angular position |

**CANrange Sensor**

| Field | Unit | Description |
|---|---|---|
| `canrangeConnected` | boolean | Whether CANrange signals are valid |
| `canrangeDistance` | Meters | Measured distance to nearest object |
| `canrangeDetected` | boolean | Whether a game piece is within threshold |

All fields are registered with `OnboardLogger` under the `"Intake"` key.

---

## IntakeIOHardware

Real-robot implementation using two CTRE TalonFX motors and a CANrange sensor.

**Intake Roller**
- Uses `VoltageOut` with FOC enabled and change detection — only sends a new command when voltage differs from last applied.

**Deploy Motor**
- Uses `DynamicMotionMagicTorqueCurrentFOC` for profiled position control, with max velocity and acceleration from `DeployConstants`. Uses the remote CANcoder (ID 29) as feedback.

**Signal Registration**
- All signals for both motors and the CANrange are registered with `StatusSignalUtil` for synchronized Rio-side updates.

---

## IntakeIOSim

Minimal simulation — no physics model.

- Stores voltage and deploy position as local state.
- `updateInputs` only reports back `intakeVoltage` and `deployPosition`; all other fields remain at their zero defaults.
- `setDeployPosition` sets position instantly with no motion profile.

---

## Intake (Subsystem)

**Constructor**
- Accepts an `IntakeIO` instance (injected).
- Tracks `reference` (the last commanded `DeployPosition`, initialized to `Stow`) and `intaking` flag (whether the intake is actively running).

**`periodic()`**
- Calls `io.updateInputs(inputs)` each loop cycle.

**Commands**

| Method | Behavior |
|---|---|
| `intakeAt(DeployPosition state)` | Sets deploy position AND runs rollers at `kIntakeVoltage`; stops rollers on end but leaves arm at position |
| `go(DeployPosition state)` | Sets deploy position only, waits until within tolerance (0.5 rot) |
| `reverse()` | Applies `kEjectVoltage` (−8 V) while active; sets voltage to 0 on end |
| `agitate()` | Repeating sequence: `intakeAt(Deployed)` for 0.5 s, then `intakeAt(Agitate)` for 0.5 s |

`intakeAt()` is the primary intake command — it simultaneously deploys the arm and runs the rollers. It uses `this.idle()` to hold the rollers while running (does not complete on its own), and stops the rollers via `finallyDo` when interrupted or the parent command ends.

**State Management**

`setIntaking(boolean v)` sets the `intaking` flag. `RunIntake` sets it to `true` on start and `false` on end. This flag gates the `intaking()` trigger.

**Triggers**

`intaking()` — returns `true` when the `intaking` flag is set. Used by `StateManager` and `RobotBindings` to gate agitation and rumble behavior.
