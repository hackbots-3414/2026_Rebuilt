# AimConstraints

## Overview

`AimConstraints` is an immutable record that defines the physical limits of the shooter mechanism. It is passed to an `AimStrategy` at construction time so the solver knows what shots are mechanically achievable.

---

## Record Fields

| Field | Type | Description |
|---|---|---|
| `minShooterAngle` | `Rotation2d` | Minimum physically achievable launch pitch |
| `maxShooterAngle` | `Rotation2d` | Maximum physically achievable launch pitch |
| `maxOutput` | `double` | Maximum achievable output (m/s or mechanism units, matching the strategy's `SpeedControl`) |

---

## `check(AimParams params)` → `boolean`

Validates a set of `AimParams` against both constraints simultaneously:

1. `params.output <= maxOutput` — shot speed is within the shooter's capability
2. `params.pitch` is within `[minShooterAngle, maxShooterAngle]` — launch angle is achievable by the hood

Returns `true` only if both conditions pass. Used by `PhysicsAim` and `ToFAim` to accept or reject candidate solutions.
