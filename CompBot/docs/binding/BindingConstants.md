# BindingConstants

## Overview

`BindingConstants` centralizes all controller port assignments and axis mappings used by the binding layer.

---

## `Driver` (inner class)

| Constant | Value | Description |
|---|---|---|
| `kDriveControllerPort` | `0` | USB port of the driver's Xbox controller |
| `kXAxis` | `1` | Raw axis index for forward/backward translation |
| `kYAxis` | `0` | Raw axis index for left/right translation |
| `kRotAxis` | `4` | Raw axis index for rotation (right stick X on Xbox) |
| `kFlipX` | `true` | Invert the X axis |
| `kFlipY` | `true` | Invert the Y axis |
| `kFlipRot` | `true` | Invert the rotation axis |

All three axes are inverted. This reconciles the Xbox joystick convention (forward/right = negative) with WPILib's field coordinate system (forward/right = positive).

---

## `Operator` (inner class)

| Constant | Value | Description |
|---|---|---|
| `kOperatorControllerPort` | `2` | USB port of the operator's PS5 controller |

No axis mappings are defined for the operator — all operator actions are button-based. See `OperatorPS5Bindings.md` for the full button map.
