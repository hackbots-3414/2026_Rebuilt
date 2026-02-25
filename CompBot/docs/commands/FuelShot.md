# FuelShot

## Overview

`FuelShot` is the complete shooting command. It runs the full aim preparation concurrently with the indexer feeding a game piece into the shooter. Implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Constructs an `AimPrep` internally and returns a `Commands.parallel(...)` of two commands:

| Command | Subsystem | Behavior |
|---|---|---|
| `AimPrep.build(...)` | Turret + Shooter | Tracks target yaw and spins flywheels/hood to aim parameters |
| `indexer.index()` | Indexer | Runs the indexer motor at `kIndexVoltage` (+0.2 V) to feed game piece |

All three subsystems run simultaneously for the duration of the command.

**Subsystems used:** `turret`, `shooter`, `indexer`

---

## Command Flow

```
FuelShot
  ├─ AimPrep
  │    ├─ turret.track(state)         ← rotate turret toward target
  │    └─ shooter.shoot(predicted)    ← spin flywheels + set hood angle
  └─ indexer.index()                  ← feed game piece into shooter
```

---

## Notes

- The indexer feeds immediately when `FuelShot` starts — there is no wait for the shooter to reach speed. If feed-gating on `tracked()` is needed, that logic should be added here or in the calling context.
- When `FuelShot` ends, the indexer stops (via its `finallyDo` handler) but the **shooter keeps spinning** (by design — `Shooter.shoot()` does not stop flywheels on end).
