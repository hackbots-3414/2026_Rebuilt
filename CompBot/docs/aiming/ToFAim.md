# ToFAim

## Overview

`ToFAim` is an `AimStrategy` that computes shot parameters by interpolating from a table of experimentally measured data points. Rather than deriving pitch and speed from physics, it looks up values from real shots taken at known distances. It compensates for robot motion by iteratively predicting where the robot will be when the game piece arrives at the target (time-of-flight recursion).

---

## Constructor

```java
public ToFAim(List<AimMeasurement> measurements, AimConstraints constraints)
```

| Parameter | Type | Description |
|---|---|---|
| `measurements` | `List<AimMeasurement>` | Experimentally collected shot data points |
| `constraints` | `AimConstraints` | Physical limits of the shooter |

On construction, the measurements are unpacked into three `InterpolatingDoubleTreeMap` instances keyed by distance (meters):

| Map | Value |
|---|---|
| `timeMap` | Time of flight (seconds) |
| `pitchMap` | Hood pitch (degrees) |
| `speedMap` | Shooter control output |

WPILib's `InterpolatingDoubleTreeMap` performs linear interpolation between the nearest neighbors for any queried distance.

---

## `update(Pose3d target, Pose3d shooter, Translation2d velocity)` → `AimParams`

Uses iterative time-of-flight recursion to find where the robot will be when the game piece arrives, then aims at the adjusted position.

**Algorithm (up to 5 iterations):**

1. Start with `afterShooting = current shooter position` (2D)
2. Compute distance from `afterShooting` to target
3. Look up time of flight `tof` from `timeMap` for that distance
4. Predict where the robot will be after `tof` seconds: `newAfterShooting = start + velocity × tof`
5. Check convergence: if `|newAfterShooting − afterShooting| < 1e-3 m`, solution found
6. Otherwise update `afterShooting = newAfterShooting` and repeat

**After convergence:**

- Interpolate `pitch` and `shooterControl` from their respective maps at the final `distance`
- Compute `yaw` as `atan2` of the vector from `afterShooting` to the target
- Set `control = MechanismControl` — output is a direct mechanism value, not m/s
- Run `constraints.check()` to set final status

If the loop does not converge within 5 iterations, returns `AimParams.impossible()`.

---

## Constants

| Name | Value | Description |
|---|---|---|
| `EPSILON` | 1e-3 m | Convergence threshold for position error |
| `ITERATIONS` | 5 | Maximum recursion iterations |

---

## Comparison with PhysicsAim

| | `PhysicsAim` | `ToFAim` |
|---|---|---|
| Basis | Analytical ballistic physics | Experimental measurements |
| `SpeedControl` | `ProjectileVelocity` (m/s) | `MechanismControl` (mechanism units) |
| Moving robot compensation | Subtracts velocity from initial v components | Iterative ToF recursion |
| Requires tuning | Descent velocity bounds | Measured data points |
| Drag modeled | No (gravity only) | Implicitly (measurements capture real-world behavior) |
