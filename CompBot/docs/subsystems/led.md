# LED Subsystem

## Overview

The LED subsystem drives a CANdle LED strip controller using a **priority-ordered state machine**. Each robot condition maps to a named `LedState` that defines a check condition and an animation. The highest-priority state whose condition is met wins each cycle.

Unlike other subsystems, the LED subsystem does **not** follow the IO-layer pattern. `LedIO` is both the interface and the hardware implementation (it directly constructs a `CANdle`), and there is no separate `LedIOSim`. In simulation or test mode, SmartDashboard boolean toggles override the state checks to enable manual testing of each animation.

---

## Files

| File | Role |
|---|---|
| `Led.java` | Subsystem — priority state machine, calls `update(StateManager)` |
| `LedIO.java` | Hardware driver — `CANdle` wrapper, animation factory |
| `LedState.java` | Interface — `check(StateManager)` and `apply(LedIO)` |
| `LedConstants.java` | CANdle ID, LED range, timing constants |
| `ledStates/` | Concrete state implementations (one per file) |

---

## LedConstants

| Constant | Value | Description |
|---|---|---|
| `kCANdleId` | `5` | CAN ID of the CANdle controller |
| `kCanbus` | Canivore (TestBot) / RIO (others) | CAN bus selection based on robot identity |
| `startIndex` | `0` | First LED index in the strip |
| `endIndex` | `53` | Last LED index in the strip |
| `endgameWarning` | `30` | Seconds remaining when endgame warning fires |
| `endgameAlert` | `15` | Seconds remaining when endgame alert fires |
| `kStrobeRate` | `4` Hz | Strobe animation frequency |
| `kFlashRate` | `2` Hz | Flash animation frequency |
| `kEpilepsyRate` | `10` Hz | Max epilepsy strobe (capped by R101) |

---

## State Hierarchy

States are evaluated top-to-bottom. The first state whose `check()` returns `true` is applied.

| Priority | State | Condition | Animation |
|---|---|---|---|
| 1 | `BadController` | Controller connectivity issue | (error indicator) |
| 2 | `TestRslEnabled` | Test mode + enabled | (test pattern) |
| 3 | `TestRsl` | Test mode | (test pattern) |
| 4 | `EndGameAlert` | ≤15 s remaining | (alert pattern) |
| 5 | `EndGameWarning` | ≤30 s remaining | (warning pattern) |
| 6 | `OurShiftIsEnding` | Alliance human player shift ending | (shift indicator) |
| 7 | `TheirShiftIsEnding` | Opponent human player shift ending | (shift indicator) |
| 8 | `HubActive` | Our hub is active (enabled) | Solid Red or Blue depending on hub color |
| 9 | `Default` | Always true (fallback) | Default pattern |

---

## `LedIO` — Animation Types

`LedIO.createAnimation(RGBWColor, AnimationType)` generates a Phoenix 6 `ControlRequest` for the given animation type:

| Type | Phoenix Control | Notes |
|---|---|---|
| `Solid` | `SolidColor` | Constant color |
| `Strobe` | `StrobeAnimation` at `kStrobeRate` | Fast blink |
| `Flash` | `StrobeAnimation` at `kFlashRate` | Slower blink |
| `Epilepsy` | `StrobeAnimation` at `kEpilepsyRate` | Maximum allowed rate |
| `Fade` | `SingleFadeAnimation` | Pulsing fade |
| `Twinkle` | `TwinkleAnimation` | Sparkle effect |
| `Larson` | `LarsonAnimation` | Bouncing "scanner" effect |
| `Flow` | `ColorFlowAnimation` | Flowing color |
| `Rainbow` | `RainbowAnimation` | Cycling rainbow |
| `Clear` | `EmptyAnimation` | All off |

---

## `update(StateManager)` Flow

Called each loop from `Superstructure.periodic()`:

```
for each state in hierarchy (highest priority first):
    check = state.check(stateManager)    [or SmartDashboard override in sim]
    if !check → skip
    if state == appliedState → break (no change needed)
    io.applyAnimation(state.apply(io))
    appliedState = state
    return
```

The short-circuit on `appliedState == state` avoids re-sending the same animation every loop cycle.

---

## Simulation

In test mode or simulation, each state gets a boolean SmartDashboard entry under `"Led/Enable <StateName>"`. Setting it to `true` overrides the real condition and forces that animation. This allows testing animations on a benched robot without satisfying the actual conditions.
