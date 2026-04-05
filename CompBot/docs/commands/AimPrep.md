# AimPrep

## Overview

`AimPrep` spins up the shooter and points the turret at the target simultaneously. It is the aiming phase of a shot — it does not fire the game piece. Implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Returns a `Commands.parallel(...)` of two commands that run concurrently:

| Command | Subsystem | Behavior |
|---|---|---|
| `turret.track(state)` | Turret | Continuously computes target yaw from `AimParams` and drives the turret to track it |
| `shooter.shoot(state::aimParams)` | Shooter | Continuously sets flywheel velocity and hood angle from current `AimParams` |

Both commands run until the parent command ends or is interrupted.

**Subsystems used:** `turret`, `shooter`

---

## Usage

`AimPrep` runs continuously while the driver holds the aim button (Right Bumper). Once `shootReady` becomes true, `RobotBindings` triggers `RunIndex` automatically to release the game piece. `AimPrep` can also be pre-activated before a shot opportunity to warm up the shooter and position the turret.

```
AimPrep
  ├─ turret.track(state)       ← rotates turret toward target yaw
  └─ shooter.shoot(aimParams)  ← spins flywheels + sets hood angle
```
