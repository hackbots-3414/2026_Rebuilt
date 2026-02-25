# BindingConstants

## Overview

`BindingConstants` centralizes all controller port assignments and axis mappings used by the binding layer.

---

## `Driver` (inner class)

| Constant | Value | Description |
|---|---|---|
| `kDriveControllerPort` | `0` | USB port of the driver's PS5 controller |
| `kXAxis` | `1` | Raw axis index for forward/backward translation |
| `kYAxis` | `0` | Raw axis index for left/right translation |
| `kRotAxis` | `3` | Raw axis index for rotation |
| `kFlipX` | `false` | Whether to invert the X axis |
| `kFlipY` | `true` | Whether to invert the Y axis |
| `kFlipRot` | `false` | Whether to invert the rotation axis |

> Note: The comment in the source warns that axis indices may not be correct and should be verified with the chosen controller.

The Y axis is flipped (`kFlipY = true`) to reconcile the joystick's convention (forward = negative) with WPILib's field coordinate system (forward = positive).

---

## `Operator` (inner class)

| Constant | Value | Description |
|---|---|---|
| `kOperatorControllerPort` | `2` | USB port of the operator's controller |

No axis or button mappings are currently defined for the operator controller.
