# Binding System — Overview

## Purpose

The binding system connects controller inputs and robot-level automation to robot actions. It is the layer between hardware (joysticks, triggers) and commands. Each `Binder` implementation owns one logical group of bindings and is registered during robot initialization.

---

## Architecture

```
RobotContainer
     │
     ├─ hidBinder       ← real: MultiBindings(DriverXboxBindings, OperatorPS5Bindings)
     │                     sim:  KeyboardBindings
     ├─ robotBinder     ← RobotBindings (auto-fire, agitate, teleop re-home)
     ├─ autogenBinder   ← AutogenBindings (PathPlanner auto commands)
     └─ namedCommandsBinder ← NamedCommandBindings
                │
                ▼
          Superstructure
          ├─ .bindDrive(vx, vy, vrot, mode)  ← sets drivetrain default command
          ├─ .build(CommandBuilder)           ← constructs + returns a command
          └─ .state                           ← StateManager (triggers, pose, aim)
```

---

## Files

| File | Type | Role |
|---|---|---|
| `Binder.java` | Interface | Contract for all binding classes |
| `BindingConstants.java` | Constants | Controller ports and axis indices |
| `DriverXboxBindings.java` | Implementation | Xbox driver controller — drive, aim, intake, reset |
| `OperatorPS5Bindings.java` | Implementation | PS5 operator controller — index, eject, agitate, utility |
| `RobotBindings.java` | Implementation | Robot automation — auto-fire, agitate, teleop re-home |
| `MultiBindings.java` | Utility | Delegates `bind()` to multiple binders |
| `KeyboardBindings.java` | Implementation | Simulation keyboard input |
| `AutogenBindings.java` | Implementation | PathPlanner named command registration |
| `NamedCommandBindings.java` | Implementation | Named commands for autonomous |

---

## Complete Button Map

### Driver (Xbox Controller — Port 0)

| Input | Type | Action |
|---|---|---|
| Left stick Y (axis 1) | Continuous | Field-centric drive — forward/backward (inverted) |
| Left stick X (axis 0) | Continuous | Field-centric drive — lateral (inverted) |
| Right stick X (axis 4) | Continuous | Field-centric drive — rotation (inverted) |
| **Right Bumper** | Toggle | `AimPrep` — turret tracking + shooter spin-up |
| **Right Trigger** | Toggle | `RunIntake` — deploy arm + run rollers |
| **Left Bumper** | On press | `ResetForwards` — resets field-relative forward direction |
| **Left Trigger** | While held | Robot-relative drive mode |
| **X button** | On press | `RetractIntake` — stows intake arm |

While `state.intaking()` is true in teleop, the controller rumbles at high strength.

### Operator (PS5 Controller — Port 2)

| Input | Type | Action |
|---|---|---|
| **R1** | While held | `RunIndex` — feeds game piece into shooter |
| **Cross (×)** | While held | `DumpFuel` — ejects game pieces |
| **Square (□)** | While held | `AgitateIntake` — oscillates intake to shake loose jammed pieces |
| **Triangle (△)** | On press | `RetractIntake` — stows intake arm |
| **L2** | On press | `EmptyHopper` — shoots until hopper is empty |

### Robot Automation (no controller input)

| Trigger | Condition | Action |
|---|---|---|
| `shootReady` | All aim conditions satisfied | Auto-fire (`RunIndex`) repeatedly |
| `shootReady` AND NOT `intaking` | On target, not intaking | `AgitateIntake` to shake fuel into position |
| Teleop start | Climber is targeting `Climbed` position | Re-home climber to `Ready` |

---

## Design Patterns

### `Binder` Interface

Each binding group implements `Binder.bind(Superstructure)`. This decouples binding logic from `RobotContainer` and allows each group to be developed, tested, and swapped independently.

### `CommandBuilder` Pattern

All commands are constructed via `superstructure.build(CommandBuilder)` rather than being instantiated directly. This ensures commands always receive the correct subsystem references and state at build time.

### Hardware/Sim Split at Bind Time

`RobotContainer` selects between `MultiBindings(DriverXbox + OperatorPS5)` on real hardware and `KeyboardBindings` in simulation. The split happens once at construction.

### Drive Mode

`DriverXboxBindings` passes a `Supplier<TeleopDriveMode>` to `bindDrive`. The left trigger activates robot-relative drive; otherwise it defaults to field-relative spin. Superstructure overrides to `SlowFieldRelativeSpin` automatically during scoring shots.
