# DriverBindings

## Overview

`DriverBindings` wires a PS5 controller to all driver-facing robot actions — driving, aiming, intaking, and climbing. Implements `Binder`.

---

## Controller

- Type: `CommandPS5Controller`
- Port: `BindingConstants.Driver.kDriveControllerPort` (port 0)

---

## Axis Configuration

| Axis | Index | Flipped | Purpose |
|---|---|---|---|
| vx (forward/back) | 1 | No | Translational X speed |
| vy (left/right) | 0 | Yes | Translational Y speed |
| vrot (rotation) | 3 | No | Rotational rate |

Each axis supplier applies a flip factor (`× −1` or `× 1`) based on the `kFlip*` constants in `BindingConstants.Driver`.

---

## Button Bindings

| Button | Binding Type | Command | Behavior |
|---|---|---|---|
| **R1** | `toggleOnTrue` | `AimPrep` | Toggles turret tracking + shooter spin-up on/off |
| **R2** | `whileTrue` | `RunIntake` | Deploys intake and runs rollers while held; retracts on release |
| **Cross (×)** | `onTrue` | `RunClimb(Home)` | Sends climber to home position (0.0 rot) |
| **Triangle (△)** | `onTrue` | `RunClimb(Ready)` | Extends climber to ready position (0.6 rot) |
| **Circle (○)** | `onTrue` | `RunClimb(Climbed)` | Pulls climber to climbed position (0.5 rot) |

---

## Drive Binding

`superstructure.bindDrive(vx, vy, vrot)` sets the drivetrain's default command to `teleopDrive(...)` using the three axis suppliers above. Field-centric perspective is automatically corrected for alliance color.

---

## Notes

- `AimPrep` uses `toggleOnTrue` — press R1 once to start tracking, press again to stop. This allows the driver to pre-aim before committing to a shot.
- `RunIntake` uses `whileTrue` — the intake only runs while R2 is physically held, and retracts immediately on release.
- All three climb buttons use `onTrue` — each triggers a single position move and completes when the climber arrives; they do not require the button to be held.
