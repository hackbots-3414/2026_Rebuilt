# Indexer Subsystem

## Overview

The Indexer subsystem controls the game piece indexing mechanism. It drives a single motor at a fixed voltage to move a game piece forward (index) or backward (eject). There is no position or velocity feedback — control is purely open-loop voltage. The subsystem follows the same IO-layer pattern as the rest of the robot.

---

## Files

| File | Role |
|---|---|
| `Indexer.java` | Subsystem class — commands and periodic loop |
| `IndexerConstants.java` | Hardware IDs, voltage setpoints, and motor config |
| `IndexerIO.java` | IO interface + logged inputs data class |
| `IndexerIOHardware.java` | Real-hardware implementation (TalonFX) |
| `IndexerIOSim.java` | Simulation implementation (DCMotorSim) |

---

## IndexerConstants

**Motor**
- CAN ID: `63`
- Motor type: TalonFX (Kraken X60 FOC)
- Neutral mode: Brake
- Inversion: Clockwise Positive

**Voltage Setpoints**

| Name | Value | Purpose |
|---|---|---|
| `kIndexVoltage` | +0.2 V | Feed game piece forward |
| `kEjectVoltage` | −0.2 V | Push game piece backward |

**Current Limits**
- Supply current limit: 40 A
- Stator current limit: 125 A

---

## IndexerIO Interface

**Methods**
- `updateInputs(IndexerIOInputs inputs)` — refreshes all sensor readings
- `setVoltage(Voltage voltage)` — open-loop voltage control
- `stop()` — stops the motor

**`IndexerIOInputs` (logged fields)**

| Field | Unit | Description |
|---|---|---|
| `motorConnected` | boolean | Whether all status signals are valid |
| `supplyCurrent` | Amps | Battery-side current draw |
| `torqueCurrent` | Amps | Motor torque current |
| `statorCurrent` | Amps | Motor stator current |
| `voltage` | Volts | Applied motor voltage |
| `velocity` | Rotations/s | Motor angular velocity |
| `temperature` | Celsius | Motor temperature |

All fields are registered with `OnboardLogger` under the `"Indexer"` key.

---

## IndexerIOHardware

Real-robot implementation using a CTRE TalonFX.

- Uses `VoltageOut` for open-loop voltage control with change detection — a new control request is only sent when the voltage differs from the last applied value.
- `stop()` calls `motor.stopMotor()` directly.
- Supply current, torque current, stator current, voltage, temperature, and velocity signals are registered with `StatusSignalUtil` for synchronized Rio-side updates.

---

## IndexerIOSim

Simulation implementation using WPILib's `DCMotorSim`.

- Models 1× Kraken X60 FOC motor with moment of inertia 2 kg·m² and gear ratio 10:1.
- `updateInputs` steps the simulation forward by one robot period and reads back current, voltage, and velocity.
- `stop()` applies 0 V to the simulated motor plant.

---

## Indexer (Subsystem)

**Constructor**
- Accepts a `IndexerIO` instance (injected — either hardware or sim).
- No SmartDashboard entries published.

**`periodic()`**
- Calls `io.updateInputs(inputs)` to refresh sensor data every loop cycle.

**Commands**

| Method | Behavior |
|---|---|
| `index()` | Runs motor at `kIndexVoltage` (+0.2 V); stops on end |
| `eject()` | Runs motor at `kEjectVoltage` (−0.2 V); stops on end |
| `stop()` | Calls `io.stop()`; also stops on end via `finallyDo` |

All three commands use `this.run(...)` (require the subsystem while active) and call `io.stop()` in their `finallyDo` handler so the motor always stops when the command ends or is interrupted.
