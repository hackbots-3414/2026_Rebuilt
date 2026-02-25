# RobotBindings

## Overview

`RobotBindings` defines robot-level automatic behavior that runs independently of driver input. It handles auto-firing when the robot is on target and re-homing the climber at the start of teleop if it was left in the climbed position. Implements `Binder`.

---

## `bind(Superstructure)` Bindings

### Auto-Fire on Aim Lock

```java
superstructure.state.shootReady()
    .debounce(0.25, DebounceType.kFalling)
    .whileTrue(superstructure.build(shoot).repeatedly())
```

- **Trigger:** `StateManager.shootReady()` — fires when the turret and shooter are both confirmed on target
- **Debounce:** 0.25 s falling-edge debounce — the trigger must be continuously `false` for 250 ms before the command stops, preventing rapid fire interruptions from small momentary misalignments
- **Command:** `FuelShot` (real robot) or `FuelShotSim` (simulation), run `repeatedly()` so it re-fires as long as `shootReady` stays true
- **Hardware/sim split:** `Robot.isReal()` selects `FuelShot` on hardware and `FuelShotSim` in simulation at bind time

### Teleop Climber Re-Home

```java
RobotModeTriggers.teleop().onTrue(
    superstructure.build(new RunClimb(ClimbPosition.Ready))
        .onlyIf(superstructure.state.climbed())
)
```

- **Trigger:** `RobotModeTriggers.teleop()` — fires once when teleop begins
- **Guard:** `.onlyIf(state.climbed())` — only executes if the climber is currently in the `Climbed` position
- **Command:** `RunClimb(Ready)` — retracts the climber back to the `Ready` position (0.6 rot) at the start of teleop, preventing the robot from starting with a fully retracted climber if it was left climbed

---

## Notes

- The 0.25 s falling debounce on `shootReady` is a deliberate design choice — it keeps the indexer running through brief tracking interruptions (e.g., from drivetrain bumps) rather than stopping and restarting the feed cycle.
- The teleop re-home guard ensures this command is a no-op in the common case where the climber was not used, avoiding unnecessary motion at match start.
