# DriverXboxBindings

## Overview

`DriverXboxBindings` wires an Xbox controller to all driver-facing robot actions — driving, aiming, intaking, and field reset. Implements `Binder`.

---

## Controller

- Type: `CommandXboxController`
- Port: `BindingConstants.Driver.kDriveControllerPort` (port 0)

---

## Axis Configuration

| Axis | Index | Flipped | Purpose |
|---|---|---|---|
| vx (forward/back) | 1 | Yes | Translational X speed |
| vy (left/right) | 0 | Yes | Translational Y speed |
| vrot (rotation) | 4 | Yes | Rotational rate |

All three axes are inverted (`kFlipX`, `kFlipY`, `kFlipRot` all `true`).

---

## Button Bindings

| Button | Binding Type | Command | Behavior |
|---|---|---|---|
| **Right Bumper** | `toggleOnTrue` | `AimPrep` | Toggles turret tracking + shooter spin-up on/off |
| **Right Trigger** | `toggleOnTrue` | `RunIntake` | Toggles deploy arm + intake rollers on/off |
| **Left Bumper** | `onTrue` | `ResetForwards` | Resets the field-relative forward direction to the current robot heading |
| **X button** | `onTrue` | `RetractIntake` | Stows the intake arm |

Left trigger is not bound to a command — instead it switches drive mode to robot-relative while held (see Drive Binding below).

---

## Drive Binding

`superstructure.bindDrive(vx, vy, vrot, mode)` sets the drivetrain's default command.

- **Default mode:** `TeleopDriveMode.FieldRelativeSpin` (normal field-centric drive)
- **Left trigger held:** `TeleopDriveMode.RobotRelative`
- **Scoring shot active:** Superstructure overrides to `SlowFieldRelativeSpin` automatically regardless of driver input

Field-centric perspective is automatically corrected for alliance color.

---

## Rumble

While `state.intaking()` is `true` in teleop, the controller rumbles at `RumbleStrength.High`. This gives the driver tactile feedback that the intake is actively running.

---

## Notes

- `AimPrep` uses `toggleOnTrue` — press Right Bumper once to start tracking, press again to stop. This allows the driver to pre-aim before committing to a shot.
- `RunIntake` also uses `toggleOnTrue` — press Right Trigger to deploy and start rollers; press again to retract. This frees the driver's hand compared to a hold-to-run binding.
- Left trigger modifies drive mode only while physically held; releasing it returns to field-relative drive.
