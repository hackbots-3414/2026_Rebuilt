# RunIntake

## Overview

`RunIntake` sets the intaking flag, then deploys the intake arm and runs the intake rollers simultaneously. Implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Returns a sequence:

```java
Commands.sequence(
    Commands.runOnce(() -> subsystems.intake().setIntaking(true)),
    subsystems.intake().intakeAt(DeployPosition.Deployed)
).finallyDo(() -> subsystems.intake().setIntaking(false));
```

1. **`setIntaking(true)`** — immediately sets the intaking flag so `StateManager.intaking()` goes true
2. **`intakeAt(Deployed)`** — deploys the arm to `0.224 rotations` and runs rollers at `kIntakeVoltage` (+12 V); holds until interrupted
3. **`setIntaking(false)`** — clears the intaking flag when the command ends or is interrupted

**Subsystems used:** `intake`

---

## Command Flow

```
RunIntake
  ├─ setIntaking(true)
  ├─ intakeAt(Deployed)   ← deploy arm + spin rollers at +12 V
  └─ finallyDo: setIntaking(false)
```

---

## Notes

- `intakeAt()` does not complete on its own — it holds the intake deployed and spinning until the command is interrupted (e.g., by the toggle binding pressing again, or a `RetractIntake` command).
- The `intaking` flag gates both the `AgitateIntake` automation in `RobotBindings` and the controller rumble in `DriverXboxBindings`.
- On end, the rollers stop (via `intakeAt`'s `finallyDo`) but the arm remains at the deployed position. Use `RetractIntake` to stow.
