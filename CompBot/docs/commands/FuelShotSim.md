# FuelShotSim

## Overview

`FuelShotSim` is a simulation-only command that launches a virtual projectile and animates its flight on the Field2d widget. It implements `CommandBuilder` and contains an inner class `FuelSim` that runs the physics model.

---

## `build(Subsystems, StateManager)` → `Command`

Schedules a `singleBuild(...)` command via `CommandScheduler` using `Commands.runOnce(...)`. This fires-and-forgets the sim command so it runs independently of the caller's command lifecycle — the projectile continues flying even after the trigger that launched it is released.

## `singleBuild(Subsystems, StateManager)` → `Command`

Creates a new `FuelSim` instance and returns:

```
Commands.sequence(
    runOnce(() -> sim.launch(state)),   ← initialize position & velocity
    Commands.run(sim::tick)             ← advance physics each loop cycle
)
.until(sim::atHub)                      ← end when projectile reaches target height
.withName("Fuel Shot (sim)")
```

---

## `FuelSim` (inner class)

A per-shot projectile physics model.

**Constants**

| Name | Value | Description |
|---|---|---|
| `dt` | 0.02 s | Simulation time step (one robot loop period) |
| `resolution` | 10 | Sub-steps per tick for numerical stability |
| `gravity` | (0, 0, −9.81) m/s² | Gravitational acceleration vector |

**`launch(StateManager state)`**

Initializes the projectile from the current `AimParams`:

1. Sets starting `position` to the turret's 3D pose translation.
2. Decomposes `AimParams.output` (m/s), `pitch`, and `yaw` into a 3D velocity vector in field-relative coordinates.
3. Adds a random error of up to ±0.15 m/s on each axis to simulate shot scatter.
4. Adds the robot's field-relative translational velocity to the projectile velocity (moving robot compensation).
5. Sets `target` to the aim target's 3D translation from `StateManager`.

> Requires `AimParams.control == SpeedControl.ProjectileVelocity`; throws `IllegalStateException` otherwise.

**`tick()`**

Advances the simulation by one robot loop period using sub-stepped Euler integration:

For each of `resolution` (10) sub-steps:
1. Update `position += velocity × (dt / resolution)`
2. Compute aerodynamic drag deceleration:
   - `Fd = −0.5 × ρ × π × r² × Cd × v²` where ρ=1.225 kg/m³, r=0.075 m, Cd=0.47
3. `velocity += (drag_acceleration + gravity) × (dt / resolution)`

After each tick, the current position is pushed to `FieldManager` as a `Pose3d` for 3D field visualization.

**`atHub()`**

Returns `true` when the projectile has descended below the target's Z height (`position.Z < target.Z`) while moving downward (`velocity.Z < 0`). This terminates the flight simulation.

---

## Physics Model Summary

```
Launch
  position  = turret 3D pose
  velocity  = f(pitch, yaw, output) + robot velocity + random noise

Each tick (×10 sub-steps per loop)
  position += velocity × Δt
  Fd        = -0.5 × 1.225 × π × 0.075² × 0.47 × |v|²
  velocity += (Fd_vector + gravity) × Δt

End condition
  position.z < target.z  AND  velocity.z < 0
```
