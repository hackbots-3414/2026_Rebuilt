# StateManager

## Overview

`StateManager` is the robot-wide shared state hub. It holds the current and predicted `AimParams`, provides computed access to robot pose, velocity, and aim target, exposes composite triggers used by the binding layer, and manages the aim parameter cache lifecycle each loop cycle.

---

## Constructor

Accepts the `Subsystems` record and registers an extensive set of `OnboardLogger` entries under the `"Robot"` key:

**Robot state**
- `Robot Pose` — current field pose
- `Robot Velocity` — field-relative velocity as Transform2d
- `Aim Target` — 3D target position (hub or feed target)
- `Turret Position` — turret's 3D field pose

**Triggers**
- `Shoot Ready` — composite on-target trigger
- `Turret Tracked` — turret within yaw tolerance
- `Shooter Tracked` — shooter within velocity + pitch tolerance

**Aim Params** (both current and predicted, under `"Aim Params/"` and `"Aim Params (Predicted)/"`)
- Status, pitch, yaw, velocity output, and all three tolerance fields

---

## Aim Parameter Caching

`StateManager` uses a **lazy-evaluation cache** pattern for aim parameters. Both `params` and `predictedParams` are reset to `AimStatus.Unchecked` at the start of every loop cycle in `periodic()`. When a consumer calls `aimParams()` or `predictedAimParams()`, the result is computed once and cached for the rest of that cycle — subsequent calls return the cached value without re-running the solver.

```
periodic()  →  params = Unchecked, predictedParams = Unchecked

aimParams() called first time this cycle:
    status == Unchecked → run solver → cache result → return

aimParams() called again this cycle:
    status != Unchecked → return cached result (no re-solve)
```

This prevents the `AimStrategy` solver from running multiple times per loop when both the turret and shooter consume aim parameters in the same cycle.

---

## Methods

### `robotPose()` → `Pose2d`
Current estimated robot pose from the drivetrain's odometry/vision fusion.

### `robotVelocity()` → `Transform2d`
Field-relative velocity (vx, vy, omega) from the drivetrain.

### `aimTarget()` → `Pose3d`
Returns the appropriate 3D target position based on field zone:
- **In alliance zone** (`x ≤ 182.11"` from own alliance wall) → `FieldUtils.hub()` — the scoring hub at (4.63, 4.01, 1.83 m) for Blue, mirrored for Red
- **Outside alliance zone** → `FieldUtils.feedTarget()` — the feed station target at (4.5, 2.0, 1.0 m) for Blue, mirrored for Red

Both are automatically flipped for Red alliance.

### `aimParams()` → `AimParams`
Returns (or lazily computes) the current-pose aim solution. Uses the current turret 3D pose and current robot velocity. Solved by `AimConstants.kAim` (`PhysicsAim` with pitch range 49.5°–72°, max speed 18, descent velocity 2–10 m/s).

### `predictedAimParams()` → `AimParams`
Returns (or lazily computes) the predicted-pose aim solution. Uses the drivetrain's one-loop-ahead predicted pose and predicted velocity. Consumed by `Shooter.shoot()` and `Turret.track()` so they lead the target while the robot is moving.

### `shootReady()` → `Trigger`
Composite trigger that is `true` only when **all three** conditions hold simultaneously:
1. `turret.tracked(this::aimParams)` — turret yaw within `deltaYaw`
2. `shooter.tracked(this::aimParams)` — shooter velocity and hood within their tolerances
3. `params.isOk()` — the aim solution is `Possible` (not `Impossible` or `Unchecked`)

Used by `RobotBindings` to gate the auto-fire command.

### `climbed()` → `Trigger`
Delegates to `climber.at(ClimbPosition.Climbed)`. Used by `RobotBindings` to guard the teleop re-home command.

### `turretPose()` → `Pose3d`
The turret's 3D field pose, derived by applying the turret's 3D offset transform to the current robot pose.

### `periodic()`
Resets both `params` and `predictedParams` to `Unchecked` each loop cycle, invalidating the cache and forcing a fresh solve on the next access.
