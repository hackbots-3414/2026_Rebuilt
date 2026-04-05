# OperatorPS5Bindings

## Overview

`OperatorPS5Bindings` wires a PS5 controller to operator-facing utility actions — indexing, ejecting, agitating, and hopper management. Implements `Binder`.

---

## Controller

- Type: `CommandPS5Controller`
- Port: `BindingConstants.Operator.kOperatorControllerPort` (port 2)

---

## Button Bindings

| Button | Binding Type | Command | Behavior |
|---|---|---|---|
| **R1** | `whileTrue` | `RunIndex` | Feeds game piece into shooter while held |
| **Cross (×)** | `whileTrue` | `DumpFuel` | Runs indexer/intake in reverse to eject game pieces while held |
| **Square (□)** | `whileTrue` | `AgitateIntake` | Oscillates the intake arm to shake loose jammed pieces while held |
| **Triangle (△)** | `onTrue` | `RetractIntake` | Stows the intake arm |
| **L2** | `onTrue` | `EmptyHopper` | Runs the indexer until the hopper is empty |

---

## Notes

- `RunIndex` is the operator's manual override for indexing. Normally auto-fire via `shootReady` handles indexing autonomously, but the operator can force-feed with R1.
- `AgitateIntake` is the operator's manual agitation override. Normally `RobotBindings` agitates automatically when `shootReady` is true, but the operator can trigger it directly if fuel is stuck.
- `DumpFuel` and `EmptyHopper` are utility commands for clearing the robot between cycles.
