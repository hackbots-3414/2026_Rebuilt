# AimStrategy

## Overview

`AimStrategy` is the interface that all aim-calculation algorithms implement. It defines a single method that takes the current robot/shooter state and returns a fully populated `AimParams` object describing the optimal shot.

---

## Interface

```java
public interface AimStrategy {
    public AimParams update(Pose3d target, Pose3d shooter, Translation2d velocity);
}
```

**Parameters**

| Parameter | Type | Description |
|---|---|---|
| `target` | `Pose3d` | 3D field position of the scoring target |
| `shooter` | `Pose3d` | 3D field position of the shooter mechanism (accounts for robot pose + turret offset) |
| `velocity` | `Translation2d` | Current field-relative translational velocity of the robot (used for moving-robot compensation) |

**Returns** — an `AimParams` with `status = Possible` if a valid shot was found, or `AimParams.impossible()` if no feasible solution exists.

---

## Implementations

| Class | Approach |
|---|---|
| `PhysicsAim` | Analytical ballistic solver — computes shot parameters from first-principles physics |
| `ToFAim` | Interpolation-based solver — looks up parameters from experimentally measured distance tables |

The active strategy is injected into `StateManager` at robot startup, allowing the approach to be swapped without touching subsystem code.
