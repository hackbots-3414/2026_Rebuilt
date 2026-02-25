# PhysicsAim

## Overview

`PhysicsAim` is an `AimStrategy` that computes shot parameters analytically using ballistic physics. Given the 3D positions of the target and shooter and the robot's velocity, it solves for the pitch, yaw, and projectile speed needed to score. It uses a binary search over the final descent velocity to find the best feasible solution within the shooter's mechanical constraints.

---

## Constructor

```java
public PhysicsAim(AimConstraints constraints, double minDescentVelocity, double maxDescentVelocity)
```

| Parameter | Type | Description |
|---|---|---|
| `constraints` | `AimConstraints` | Physical limits of the shooter (pitch range, max speed) |
| `minDescentVelocity` | `double` | Minimum acceptable downward speed (m/s) at the target |
| `maxDescentVelocity` | `double` | Maximum acceptable downward speed (m/s) at the target |

The descent velocity range controls how steeply the projectile can arrive at the target — a higher descent velocity means a steeper arc.

---

## `update(Pose3d target, Pose3d shooter, Translation2d velocity)` → `AimParams`

**Step 1 — Solve boundary cases**

Calls `quicksolve` twice — once at `minDescentVelocity` and once at `maxDescentVelocity` — to find the pitch range the solver is working within.

**Step 2 — Check feasibility**

If the pitch range produced by the descent velocity range does not overlap the shooter's physically achievable pitch range (`AimConstraints`), returns `AimParams.impossible()` immediately.

**Step 3 — Try minimum descent first**

If the minimum descent velocity already produces a valid solution (passes `constraints.check()`), returns that directly — preferring a flatter, faster trajectory.

**Step 4 — Binary search (5 iterations)**

If the minimum doesn't satisfy constraints, performs a binary search over the descent velocity range to find a feasible solution:
- If `constraints.check()` passes → narrows upper bound, saves as `best`
- If pitch is too high → narrows upper bound
- If pitch is too low → narrows lower bound
- If output exceeds `maxOutput` → narrows upper bound

**Step 5 — Yaw sanity check**

After finding a candidate, verifies the computed yaw is pointing within ~144° (0.8π rad) of the direct line to the target. If the yaw would require shooting nearly backwards, returns `AimParams.impossible()`.

---

## `quicksolve(Translation3d offset, Translation2d robotVelocity, double finalDescentSpeed)` → `AimParams` *(static)*

The core analytical solver. Given the 3D displacement to the target, robot velocity, and a desired final descent speed, solves for initial velocity components using projectile motion under gravity.

**Physics derivation:**

Assuming only gravity (no drag):

1. **Solve for time of flight** using the vertical component:
   - The projectile must travel `dz` vertically with final downward velocity `v_zf`
   - Solve `dz = v_z0 * t - 0.5 * g * t²` with `v_zf = v_z0 - g*t`
   - Rearranges to a quadratic: `0.5g*t² - v_zf*t - dz = 0`
   - Takes the positive root

2. **Solve for horizontal initial velocities:**
   - `vx = dx / t`, `vy = dy / t`
   - `vz = g*t - v_zf`

3. **Subtract robot velocity** from horizontal components (moving-robot compensation):
   - `vx -= robotVelocity.x`, `vy -= robotVelocity.y`

4. **Derive pitch and yaw:**
   - Total speed: `v = √(vx² + vy² + vz²)`
   - Yaw: `atan2(vy, vx)`
   - Pitch: `asin(vz / v)`

Returns an `AimParams` with `output`, `pitch`, and `yaw` populated (status left as `Unchecked`).

---

## Constants

| Name | Value | Description |
|---|---|---|
| `ITERATIONS` | 5 | Number of binary search iterations |

5 iterations gives ~3% precision on the descent velocity interval, which is sufficient given mechanical tolerances.
