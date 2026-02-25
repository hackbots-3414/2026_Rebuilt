# PhysicsAimTest

## Overview

Unit tests for the `PhysicsAim` aiming system using JUnit 5. Covers `AimParams` status management, solver monotonicity, and angle arithmetic edge cases.

---

## Tests

### `setStatusTest`

Verifies that `AimParams` status transitions work correctly:
- A freshly constructed `AimParams` starts with `status = Unchecked`
- Setting `status = Possible` causes `isOk()` to return `true`
- Setting `status = Impossible` causes `isOk()` to return `false`

### `ensureMonotonic`

Verifies that `PhysicsAim.quicksolve` produces a **monotonically increasing pitch** as final descent velocity increases, for a fixed offset of (1, 1, 3) meters with zero robot velocity.

- Sweeps `finalDescentSpeed` from 0.0 to 5.0 in 0.1 steps
- Asserts each successive pitch is strictly greater than the last

This is an important invariant — the binary search in `PhysicsAim.update()` relies on pitch being monotonically related to descent velocity to converge correctly.

### `testAngleRounding`

Verifies that `180°` and `−180°` are treated as equivalent angles when computing differences via `MathUtil.angleModulus`.

- Computes `Rotation2d.fromDegrees(180).minus(Rotation2d.fromDegrees(-180))`
- Applies `MathUtil.angleModulus` and asserts the result is within 1e-3 radians of zero

This guards against the yaw sanity check in `PhysicsAim.update()` incorrectly rejecting shots near the ±180° wrap point.
