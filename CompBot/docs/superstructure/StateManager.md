# StateManager

## Overview

`StateManager` is the robot-wide shared state hub. It manages the current `AimParams`, shoot mode selection, composite triggers, and auto-chooser state. It is the primary interface through which the binding layer observes robot state and gates behavior.

---

## `ShootMode` Enum

`ShootMode` describes what the robot *wants* to be doing at any given moment. It drives both aim target selection and drive behavior.

| Value | Meaning |
|---|---|
| `Scoring` | In alliance zone, conditions allow a shot at the hub |
| `Feeding` | Outside alliance zone, feeding game pieces to the alliance |
| `Donut` | "Do not shoot" — no valid shot target; aim returns `Impossible` |

`calculateWantedShootMode()` runs each loop and selects the mode automatically:
- In autonomous: `Scoring` if in alliance zone, `Donut` otherwise (no auto-feed yet)
- In teleop, outside alliance zone: `Feeding` unless in no-feed zone (→ `Donut`)
- In teleop, inside alliance zone: `Donut` if in tower zone or if our alliance's human player is not active; `Scoring` otherwise (uses `ActivityCalculator` with a 2-second window)

---

## Constructor

Accepts the `Subsystems` record and registers `OnboardLogger` entries under the `"Robot"` key:

**Robot state**
- `Robot Pose` — current field pose
- `Robot Velocity` — field-relative velocity as Transform2d
- `Turret Position` — turret's 3D field pose
- `In Alliance Zone` — boolean field zone check

**Triggers**
- `Shoot Ready` — composite on-target trigger
- `Turret Tracked` — turret within yaw tolerance
- `Shooter Tracked` — shooter within velocity + pitch tolerance

**Aim Params** (under `"Robot/Aim Params/"`)
- Status, pitch, yaw, velocity output, and all three tolerance fields

---

## Aim Parameter Caching

`StateManager` uses a **lazy-evaluation cache** pattern for aim parameters. `params` is reset to `AimStatus.Unchecked` at the start of every `update()` call, then immediately re-solved. When a consumer calls `aimParams()`, the cached result is returned if it has already been solved this cycle.

```
update()
  → reset params to Unchecked
  → call aimParams() → solve based on wantedShootMode → cache
  → recalculate wantedShootMode for the next cycle

Any other consumer calls aimParams() this cycle:
  → params.status != Unchecked → return cached result (no re-solve)
```

If `wantedShootMode == Donut`, `aimParams()` immediately returns `AimParams.impossible()` without invoking the solver.

---

## Methods

### `robotPose()` → `Pose2d`
Current estimated robot pose from the drivetrain's odometry/vision fusion.

### `robotVelocity()` → `Transform2d`
Field-relative velocity (vx, vy, omega) from the drivetrain.

### `aimParams()` → `AimParams`
Returns (or lazily computes) the current aim solution based on `wantedShootMode`:
- `Donut` → `AimParams.impossible()`
- `Scoring` → `AimConstants.kScoringAim.update(FieldUtils.hub(), turretPose, velocity)` — uses `ToFAim` with scoring lookup table
- `Feeding` → `AimConstants.kFeedingAim.update(FieldUtils.feedTarget(robotPose), turretPose, velocity)` — uses `ToFAim` with feeding lookup table

### `turretPose()` → `Pose3d`
The turret's 3D field pose, derived by applying the turret's 3D offset transform to the current robot pose.

### `shootReady` → `Trigger`
Composite trigger that is `true` only when **all four** conditions hold simultaneously:

```
shootReady
    ├─ aimOk                                           ← AimStatus == Possible
    ├─ turret.tracked(aimParams)  debounced 0.1s fall  ← turret yaw within deltaYaw
    ├─ shooter.tracked(aimParams) debounced 1.5s fall  ← shooter velocity + pitch within tolerance
    └─ validOdometry.or(!FORCE_ODOMETRY)               ← drivetrain has recent vision update
```

The long shooter debounce (1.5 s falling) prevents the auto-fire command from cycling off-and-on if the shooter dips below tolerance momentarily while the robot bumps.

### `shooting()` → `Trigger`
True whenever the shooter subsystem reports it is actively shooting.

### `shooting(ShootMode mode)` → `Trigger`
True whenever the shooter is shooting AND `wantedShootMode == mode`. Used by the drivetrain to engage slow drive mode only during scoring shots.

### `shouldAgitate()` → `Trigger`
Returns `shootReady`. Used by `RobotBindings` to gate the agitate command.

### `climbing()` → `Trigger`
True when the climber is targeting `ClimbPosition.Climbed`. Used by `RobotBindings` to guard the teleop re-home command (prevents re-homing if the driver never pressed climb).

### `climbed()` → `Trigger`
True when the climber has reached `ClimbPosition.Climbed`.

### `intaking()` → `Trigger`
True when `Intake.intaking` flag is set (i.e., `RunIntake` is active).

### `initAutoChooser()`
Builds the autonomous routine chooser via `BetterAutoChooser.buildAutoChooser()` and stores it internally. Called once during `RobotContainer` construction.

### `getAuton()` → `Command`
Returns the currently selected autonomous routine from the chooser.

### `inAutonStartPose` → `Trigger`
True when the robot's current pose matches the expected starting pose for the selected autonomous routine (via `BetterAutoChooser.checkPose`).

### `update()`
Called each loop from `Superstructure.periodic()`:
1. Resets `params` to `Unchecked`
2. Calls `aimParams()` to eagerly solve and cache aim parameters
3. Recalculates `wantedShootMode` for the next cycle
