# RunIntake

## Overview

`RunIntake` deploys the intake arm and runs the intake rollers simultaneously. Implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Returns a `Commands.parallel(...)` of two commands:

| Command | Subsystem | Behavior |
|---|---|---|
| `intake.go(DeployPosition.Deployed)` | Intake (deploy motor) | Rotates arm to the `Deployed` position (1.0 rotations); waits until within tolerance |
| `intake.intake()` | Intake (roller motor) | Spins rollers at `kIntakeVoltage` (+5 V) to ingest a game piece |

Both run concurrently — the rollers spin while the arm is still deploying.

**Subsystems used:** `intake`

---

## Command Flow

```
RunIntake
  ├─ intake.go(Deployed)    ← deploy arm to 1.0 rotations
  └─ intake.intake()        ← spin rollers at +5 V
```

---

## Notes

- The command ends when the parent ends or is interrupted. On end, `intake.intake()` stops the rollers (via its `startEnd` handler), but the arm stays at the deployed position — retraction requires a separate `intake.go(Stow)` call.
- No jam detection is handled here; the `detectJam()` trigger on the `Intake` subsystem must be composed externally if unjam behavior is needed.
