# Climber Subsystem

## Overview

The Climber subsystem controls the robot's climbing mechanism. It drives a single motor to one of three predefined angular positions using motion-profiled position control. The subsystem follows the IO-layer pattern, separating hardware and simulation implementations behind a common interface.

---

## Files

| File | Role |
|---|---|
| `Climber.java` | Subsystem class — commands, triggers, periodic loop |
| `ClimberConstants.java` | Hardware IDs, tuning values, and position presets |
| `ClimberIO.java` | IO interface + logged inputs data class |
| `ClimberIOHardware.java` | Real-hardware implementation (TalonFX) |
| `ClimberIOSim.java` | Simulation implementation (DCMotorSim) |

---

## ClimberConstants

**Motor**
- CAN ID: `64`
- Motor type: TalonFX (Kraken X60 FOC)
- Neutral mode: Brake
- Inversion: Clockwise Positive

**Position Presets (`ClimbPosition` enum)**

| Name | Value |
|---|---|
| `Home` | 0.0 rotations |
| `Ready` | 0.6 rotations |
| `Climbed` | 0.5 rotations |

**Motion Profile**
- Cruise velocity: 32 rot/s
- Acceleration: 40 rot/s²
- At-position tolerance: π/4 radians (~45°)

**Current Limits**
- Supply current limit: 40 A
- Stator current limit: 125 A

---

## ClimberIO Interface

Defines the contract all IO implementations must fulfill.

**Methods**
- `updateInputs(ClimberIOInputs inputs)` — refreshes all sensor readings into the inputs object
- `setVoltage(Voltage voltage)` — open-loop voltage control
- `setPosition(Angle position)` — closed-loop position control

**`ClimberIOInputs` (logged fields)**

| Field | Unit | Description |
|---|---|---|
| `motorConnected` | boolean | Whether all status signals are valid |
| `supplyCurrent` | Amps | Battery-side current draw |
| `torqueCurrent` | Amps | Motor torque current |
| `statorCurrent` | Amps | Motor stator current |
| `voltage` | Volts | Applied motor voltage |
| `velocity` | Rotations/s | Motor angular velocity |
| `temperature` | Celsius | Motor temperature |
| `position` | Radians | Motor angular position |

All fields are registered with `OnboardLogger` under the `"Climber"` key for telemetry logging.

---

## ClimberIOHardware

Real-robot implementation using a CTRE TalonFX.

**Control Mode**
- Position control uses `DynamicMotionMagicTorqueCurrentFOC`, with cruise velocity and acceleration pulled from `ClimberConstants`.
- Voltage control uses `VoltageOut` and only sends a new command when the requested voltage differs from the last applied voltage (change detection).

**Signal Registration**
- Supply current, torque current, stator current, voltage, temperature, velocity, and position signals are all registered with `StatusSignalUtil` for synchronized Rio-side updates.

---

## ClimberIOSim

Simulation implementation using WPILib's `DCMotorSim`.

- Models 1× Kraken X60 FOC motor with moment of inertia 2 kg·m² and gear ratio 10:1.
- `updateInputs` steps the simulation forward by one robot period and reads back position, current, voltage, and velocity.
- `setPosition` sets the target position directly (no motion profile in sim — position is stored and reported immediately).
- `setVoltage` applies voltage directly to the simulated motor plant.

---

## Climber (Subsystem)

**Constructor**
- Accepts a `ClimberIO` instance (injected — either hardware or sim).
- Publishes SmartDashboard buttons for `Home`, `Ready`, and `Climbed` positions.

**`periodic()`**
- Calls `io.updateInputs(inputs)` to refresh sensor data every loop cycle.
- Publishes current position in degrees to `SmartDashboard` under `"Climber/ClimbLevel"`.

**`go(ClimbPosition climbLevel)` → `Command`**
- Sends a position setpoint to the IO layer.
- Waits until the `at(climbLevel)` trigger fires (i.e., position is within tolerance).
- Returns a sequential command: `runOnce(setPosition) → waitUntil(atPosition)`.

**`at(ClimbPosition climbLevel)` → `Trigger`**
- Returns a `Trigger` that is `true` when `|currentPosition - targetPosition| ≤ kTolerance` (in radians).
- Can be composed into other command groups or used for state-based logic elsewhere.

---

## Command Flow

```
Driver input / auto
       │
       ▼
Climber.go(ClimbPosition)
       │
       ├─ io.setPosition(climbLevel.position)   ← sends Motion Magic setpoint
       │
       └─ waitUntil(at(climbLevel))             ← blocks until within ±45°
```
