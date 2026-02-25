# Intake Subsystem

## Overview

The Intake subsystem controls two independent mechanisms: a **roller motor** that spins to ingest or eject game pieces, and a **deploy motor** that rotates the intake arm between stowed and deployed positions. A **CANrange** proximity sensor detects whether a game piece is present. The subsystem also provides jam detection based on current draw, velocity, and sensor state.

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
| `kIntakeVoltage` | +5.0 V | Spin rollers inward to ingest |
| `kEjectVoltage` | −5.0 V | Spin rollers outward to eject |

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
- Neutral mode: Brake
- Inversion: Clockwise Positive
- Supply current limit: 80 A
- Stator current limit: 120 A
- PID gains (Slot 0): all zeroed (kP, kI, kD, kS, kV, kA = 0)
- Max velocity: 0.4 rot/s
- Max acceleration: 4 rot/s²
- At-position tolerance: 0.02 rotations

**Deploy Position Presets (`DeployPosition` enum)**

| Name | Value |
|---|---|
| `Stow` | 0.0 rotations |
| `Deployed` | 1.0 rotations |

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
- Uses `DynamicMotionMagicTorqueCurrentFOC` for profiled position control, with max velocity and acceleration from `DeployConstants`.

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
- Tracks `reference` (the last commanded `DeployPosition`, initialized to `Stow`).

**`periodic()`**
- Calls `io.updateInputs(inputs)` each loop cycle.

**Commands**

| Method | Behavior |
|---|---|
| `intake()` | Applies `kIntakeVoltage` (+5 V) while active; sets voltage to 0 on end |
| `reverse()` | Applies `kEjectVoltage` (−5 V) while active; sets voltage to 0 on end |
| `go(DeployPosition)` | Sets deploy position setpoint, waits until within tolerance (0.02 rot) |

Both `intake()` and `reverse()` use `startEnd(...)` — they hold voltage for the duration of the command and stop when the command ends or is interrupted.

`go()` uses a `runOnce → waitUntil` sequence and updates the internal `reference` field so `deployAtPosition()` compares against the correct target.

**Triggers**

`detectJam()` — returns `true` when **all three** of the following are simultaneously true:
1. Intake stator current > 70 A
2. CANrange detects a game piece (`canrangeDetected == true`)
3. Intake velocity < 0.3 rot/s

This can be composed externally to trigger an unjam routine or alert.
