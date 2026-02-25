# AimPrep

## Overview

`AimPrep` spins up the shooter and points the turret at the target simultaneously. It is the aiming phase of a shot — it does not fire the game piece. Implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Returns a `Commands.parallel(...)` of two commands that run concurrently:

| Command | Subsystem | Behavior |
|---|---|---|
| `turret.track(state)` | Turret | Continuously computes target yaw from `AimParams` and drives the turret to track it |
| `shooter.shoot(state::predictedAimParams)` | Shooter | Continuously sets flywheel velocity and hood angle from predicted `AimParams` |

Both commands run until the parent command ends or is interrupted.

**Subsystems used:** `turret`, `shooter`

---

## Usage

`AimPrep` is composed into `FuelShot` — the full shot sequence runs `AimPrep` in parallel with the indexer. It can also be run standalone to pre-warm the shooter and aim the turret before a driver decides to fire.

```
AimPrep
  ├─ turret.track(state)         ← rotates turret toward target yaw
  └─ shooter.shoot(predicted)    ← spins flywheels + sets hood angle
```
