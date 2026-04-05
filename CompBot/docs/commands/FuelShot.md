# FuelShot / RunIndex

## Overview

The auto-fire shot is handled by **`RunIndex`**, which runs the indexer motor once to feed a game piece into the shooter. It is scheduled automatically by `RobotBindings` whenever `shootReady` is true. There is no separate `FuelShot.java` class in the current codebase — the full shoot sequence is decomposed across `AimPrep` (for aiming), `RobotBindings` (for the `shootReady` trigger), and `RunIndex` (for releasing the game piece).

---

## `RunIndex` — `build(Subsystems, StateManager)` → `Command`

Simply delegates to `subsystems.indexer().index()`:

```java
return subsystems.indexer().index();
```

Runs the indexer motor at `kIndexVoltage` (+0.2 V) for the duration of the command, stopping on end.

**Subsystems used:** `indexer`

---

## Full Shot Sequence (Distributed)

The complete shot is assembled across several classes:

```
AimPrep (driver toggles on R1)
  ├─ turret.track(state)       ← rotates turret toward target
  └─ shooter.shoot(aimParams)  ← spins flywheels + sets hood angle

RobotBindings (automatic)
  └─ shootReady.whileTrue(RunIndex.repeatedly())  ← feeds game piece once on target
```

`AimPrep` runs continuously while the driver holds the aim button. Once `shootReady` goes true (turret, shooter, and odometry all confirmed), `RobotBindings` triggers `RunIndex` repeatedly to fire.

---

## ShootWhenReady

`ShootWhenReady.java` is also available as an alternative: it waits for `state.shootReady` internally before running `indexer.index()`, plus plays the `FuelShotSim` animation in simulation. It is not currently wired to a controller button by default but can be used in autonomous routines.
