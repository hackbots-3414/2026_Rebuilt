# RobotBindings

## Overview

`RobotBindings` defines robot-level automatic behavior that runs independently of driver or operator input. It handles auto-firing when the robot is on target, agitating fuel when ready to shoot, and re-homing the climber at the start of teleop. Implements `Binder`.

---

## `bind(Superstructure)` Bindings

### Auto-Fire on Aim Lock

```java
superstructure.state.shootReady.whileTrue(superstructure.build(shoot).repeatedly())
```

- **Trigger:** `StateManager.shootReady` — fires when turret, shooter, odometry, and aim solution are all confirmed valid (debouncing is built into `initShootReady()` in StateManager)
- **Command:** `RunIndex`, run `repeatedly()` so it re-fires continuously as long as `shootReady` stays true
- **Note:** Both real robot and simulation currently use `RunIndex`

### Agitate on Aim Lock (Not Intaking)

```java
superstructure.state.shootReady
    .and(superstructure.state.intaking().negate())
    .whileTrue(superstructure.build(new AgitateIntake()))
```

- **Trigger:** `shootReady` AND NOT `intaking()`
- **Behavior:** While the robot is on target and not actively intaking, the intake agitates to shake fuel into the indexer. This runs concurrently with `RunIndex`.

### Teleop Climber Re-Home

```java
RobotModeTriggers.teleop().onTrue(
    superstructure.build(new RunClimb(ClimbPosition.Ready)).onlyIf(superstructure.state.climbing())
)
```

- **Trigger:** `RobotModeTriggers.teleop()` — fires once when teleop begins
- **Guard:** `.onlyIf(state.climbing())` — only executes if the climber is *targeting* the `Climbed` position (i.e., the driver pressed the climb button during auto or sandstorm)
- **Command:** `RunClimb(Ready)` — retracts the climber back to the `Ready` position at the start of teleop

---

## Notes

- The `shootReady` debouncing is handled inside `StateManager.initShootReady()` (1.5 s falling on shooter, 0.1 s falling on turret). `RobotBindings` does not apply additional debounce.
- The teleop re-home guard uses `climbing()` (wants to climb) rather than `climbed()` (has fully climbed) — this ensures re-homing even if the climber was commanded but didn't finish before teleop started.
