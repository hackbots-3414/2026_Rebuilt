# RunClimb

## Overview

`RunClimb` drives the climber to a specified position. It is parameterized at construction time with a `ClimbPosition`, making it reusable for any of the climber's presets. Implements `CommandBuilder`.

---

## Constructor

```java
public RunClimb(ClimbPosition climberLevel)
```

| Parameter | Type | Description |
|---|---|---|
| `climberLevel` | `ClimberConstants.ClimbPosition` | The target position: `Home`, `Ready`, or `Climbed` |

---

## `build(Subsystems, StateManager)` → `Command`

Delegates directly to `Climber.go(climberLevel)`, which:
1. Sends a Motion Magic position setpoint to the climber motor.
2. Waits until the climber is within tolerance (π/4 rad) of the target.

**Subsystems used:** `climber`

---

## Usage

Instantiate with the desired position and pass to the scheduler or bind to a button:

| Instance | Behavior |
|---|---|
| `new RunClimb(ClimbPosition.Home)` | Return climber to 0.0 rotations |
| `new RunClimb(ClimbPosition.Ready)` | Extend to 0.6 rotations (pre-climb) |
| `new RunClimb(ClimbPosition.Climbed)` | Pull to 0.5 rotations (climbed) |
