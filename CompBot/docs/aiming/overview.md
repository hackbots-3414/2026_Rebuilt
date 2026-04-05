# Aiming System — Overview

## Purpose

The aiming system is responsible for computing the shot parameters needed to score a game piece from any position on the field while the robot is moving. Given the current positions of the shooter and target plus the robot's velocity, it produces an `AimParams` object containing the exact pitch, yaw, and speed the shooter and turret must achieve.

---

## Architecture

```
StateManager
     │
     │  calls update() each loop
     ▼
AimStrategy  ◄──── injected at startup (PhysicsAim or ToFAim)
     │
     │  returns
     ▼
AimParams  ──────► Shooter.shoot(params)   ← sets flywheel velocity + hood angle
           ──────► Turret.track(state)     ← sets turret yaw
           ──────► Shooter.tracked(params) ← gate trigger (velocity + hood within tolerance)
           ──────► Turret.tracked(params)  ← gate trigger (yaw within tolerance)
```

---

## Files

| File | Type | Role |
|---|---|---|
| `AimParams.java` | Data class | Shot parameter container — pitch, yaw, output, tolerances, status |
| `AimConstraints.java` | Record | Physical shooter limits — min/max pitch, max output |
| `AimMeasurement.java` | Record | A single experimental data point — distance → pitch + speed + ToF |
| `AimStrategy.java` | Interface | Contract for all aim solvers |
| `PhysicsAim.java` | Implementation | Analytical ballistic solver |
| `ToFAim.java` | Implementation | Interpolation-based solver using experimental measurements |
| `PhysicsAimTest.java` | Test | Unit tests for solver correctness and invariants |

---

## Data Flow

```
Robot state (pose, velocity, target position)
           │
           ▼
    AimStrategy.update(target, shooter, velocity)
           │
     ┌─────┴──────┐
     │             │
PhysicsAim      ToFAim
(physics solver) (lookup tables)
     │             │
     └─────┬───────┘
           │
           ▼
      AimParams
  ┌────────────────────────┐
  │ status   = Possible    │
  │ pitch    = X degrees   │
  │ yaw      = Y degrees   │
  │ output   = Z m/s       │
  │ deltaPitch, deltaYaw,  │
  │ deltaOutput (tolerances│
  └────────────────────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
 Shooter        Turret
(velocity+hood) (rotation)
```

---

## The Two Strategies

### PhysicsAim — Analytical Ballistic Solver

Derives shot parameters from first principles. Given the 3D offset to the target and the robot's velocity:

1. Uses a desired **final descent velocity** as the free parameter
2. Solves a quadratic for **time of flight**
3. Back-calculates the required **initial velocity vector** (vx, vy, vz)
4. Subtracts robot velocity for moving-robot compensation
5. Converts to **pitch** (`asin(vz/v)`) and **yaw** (`atan2(vy, vx)`)
6. If the minimum descent velocity already satisfies constraints — done
7. Otherwise **binary searches** over descent velocity (5 iterations) to find the best feasible solution
8. Final **yaw sanity check** rejects solutions pointing more than ~144° away from the target

**Produces:** `SpeedControl.ProjectileVelocity` — output is in m/s.

**Pros:** Works at any distance without prior measurements. Naturally handles moving robots.
**Cons:** Ignores aerodynamic drag; real-world shots may differ at high speeds or long distances.

### ToFAim — Interpolation-Based Solver

Uses experimentally measured data instead of derived physics:

1. Accepts a `List<AimMeasurement>` — real shots taken at known distances
2. Builds three interpolation tables: **time-of-flight**, **pitch**, and **shooter control** vs. distance
3. At runtime, iteratively converges on where the robot will be when the projectile arrives:
   - Look up ToF for current distance → predict new robot position → recompute distance → repeat until `< 1mm` error (max 5 iterations)
4. Looks up final pitch and speed from tables at the converged distance
5. Runs `constraints.check()` to validate

**Produces:** `SpeedControl.MechanismControl` — output is a direct shooter mechanism value.

**Pros:** Implicitly captures real-world effects (drag, mechanical losses). Highly accurate if well-calibrated.
**Cons:** Requires careful experimental data collection; can only interpolate within the measured distance range.

---

## AimParams Lifecycle

```
Created by AimStrategy          status = Unchecked (default)
      │
      ├─► constraints.check() passes   → status = Possible
      │
      └─► no valid solution found      → status = Impossible
                                          (use AimParams.impossible())

Consumed by subsystems:
  params.isOk()          → guard before applying to hardware
  Shooter.tracked()      → checks velocity + hood within deltaPitch / deltaOutput
  Turret.tracked()       → checks yaw within deltaYaw
```

---

## Active Strategies

As of the current codebase, both scoring and feeding use `ToFAim` (interpolation-based):

| Mode | Strategy | Data source |
|---|---|---|
| `Scoring` | `ToFAim` | `ShooterConstants.scoringMeasurements` (11 data points, 1.7–6.84 m) |
| `Feeding` | `ToFAim` | `ShooterConstants.feedingMeasurements` (12 data points, including a 15 m feed shot) |

`PhysicsAim` is implemented and tested but not currently used for either mode. `TuneAim` is a tuning utility available as a commented-out alternative.

---

## Tolerances

Default tolerances on `AimParams` define when the robot is considered "on target":

| Field | Default | Used by |
|---|---|---|
| `deltaPitch` | ±4° | `Shooter.tracked()` |
| `deltaYaw` | ±2° | `Turret.tracked()` |
| `deltaOutput` | ±0.35 | `Shooter.tracked()` |

Both `Shooter.tracked()` and `Turret.tracked()` must be true simultaneously (along with `params.isOk()` and valid odometry) before the auto-fire trigger releases a game piece.

---

## Testing

`PhysicsAimTest` validates three key properties:
- `AimParams` status transitions are correct
- `quicksolve` pitch is **monotonically increasing** with descent velocity (required for binary search correctness)
- Angle arithmetic handles the ±180° wrap correctly (guards against false "impossible" near the wrap point)
