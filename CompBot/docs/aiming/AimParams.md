# AimParams

## Overview

`AimParams` is the central data object of the aiming system. It represents the complete set of shot parameters required at a particular moment in time — pitch, yaw, output speed, tolerances, and validity status. It is produced by an `AimStrategy` and consumed by the `Shooter` and `Turret` subsystems.

---

## Fields

| Field | Type | Default | Description |
|---|---|---|---|
| `status` | `AimStatus` | `Unchecked` | Validity of this params object |
| `control` | `SpeedControl` | `ProjectileVelocity` | How `output` should be interpreted |
| `pitch` | `Rotation2d` | 0° | Launch angle of the game piece out of the robot |
| `yaw` | `Rotation2d` | 0° | Field-relative heading the shooter should face |
| `output` | `double` | 0.0 | Desired speed — m/s if `ProjectileVelocity`, mechanism units if `MechanismControl` |
| `deltaPitch` | `Rotation2d` | ±4° | Tolerated pitch error for `tracked()` checks |
| `deltaYaw` | `Rotation2d` | ±2° | Tolerated yaw error for `tracked()` checks |
| `deltaOutput` | `double` | ±0.35 | Tolerated velocity error for `tracked()` checks |

---

## Enums

### `AimStatus`

| Value | Meaning |
|---|---|
| `Unchecked` | Default — validity not yet evaluated |
| `Impossible` | No valid shot exists; values are invalid and must not be used |
| `Possible` | A valid shot has been calculated |

`isOk()` returns `true` only for `Possible`.

### `SpeedControl`

| Value | Meaning |
|---|---|
| `ProjectileVelocity` | `output` is the desired projectile speed in m/s |
| `MechanismControl` | `output` is a direct shooter mechanism control input |

`PhysicsAim` produces `ProjectileVelocity` params. `ToFAim` produces `MechanismControl` params.

---

## Factory Methods

| Method | Returns |
|---|---|
| `AimParams.impossible()` | A new `AimParams` with `status = Impossible` |

---

## Usage

`AimParams` objects flow from an `AimStrategy` implementation through `StateManager` to the `Shooter` and `Turret` subsystems via `Supplier<AimParams>`. The `Shooter.tracked()` and `Turret.tracked()` triggers compare live sensor readings against the tolerance fields to determine when the robot is on target.
