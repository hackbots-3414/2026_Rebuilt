# Binding System — Overview

## Purpose

The binding system connects controller inputs and robot-level automation to robot actions. It is the layer between hardware (joysticks, triggers) and commands. Each `Binder` implementation owns one logical group of bindings and is registered during robot initialization.

---

## Architecture

```
RobotContainer
     │
     ├─ new DriverBindings().bind(superstructure)   ← driver controller
     └─ new RobotBindings().bind(superstructure)    ← autonomous robot behavior
                │
                ▼
          Superstructure
          ├─ .bindDrive(vx, vy, vrot)      ← sets drivetrain default command
          ├─ .build(CommandBuilder)         ← constructs + returns a command
          └─ .state                         ← StateManager (triggers, pose, aim)
```

---

## Files

| File | Type | Role |
|---|---|---|
| `Binder.java` | Interface | Contract for all binding classes |
| `BindingConstants.java` | Constants | Controller ports and axis indices |
| `DriverBindings.java` | Implementation | PS5 driver controller — drive, aim, intake, climb |
| `RobotBindings.java` | Implementation | Robot automation — auto-fire, teleop re-home |

---

## Complete Button Map

### Driver (PS5 Controller — Port 0)

| Input | Type | Action |
|---|---|---|
| Left stick Y (axis 0) | Continuous | Field-centric drive — lateral (Y flipped) |
| Left stick X (axis 1) | Continuous | Field-centric drive — forward/backward |
| Right stick X (axis 3) | Continuous | Field-centric drive — rotation |
| **R1** | Toggle | `AimPrep` — turret tracking + shooter spin-up |
| **R2** | While held | `RunIntake` — deploy arm + run rollers |
| **Cross (×)** | On press | `RunClimb(Home)` — climber to 0.0 rot |
| **Triangle (△)** | On press | `RunClimb(Ready)` — climber to 0.6 rot |
| **Circle (○)** | On press | `RunClimb(Climbed)` — climber to 0.5 rot |

### Robot Automation (no controller input)

| Trigger | Condition | Action |
|---|---|---|
| `shootReady()` (debounced 250 ms falling) | Turret + shooter both on target | Auto-fire (`FuelShot` or `FuelShotSim`) repeatedly |
| Teleop start | Climber is in `Climbed` position | Re-home climber to `Ready` |

---

## Design Patterns

### `Binder` Interface

Each binding group implements `Binder.bind(Superstructure)`. This decouples binding logic from `RobotContainer` and allows each group to be developed, tested, and swapped independently. Adding a new operator controller, for example, only requires a new `Binder` class.

### `CommandBuilder` Pattern

All commands are constructed via `superstructure.build(CommandBuilder)` rather than being instantiated directly. This ensures commands always receive the correct subsystem references and state at build time.

### Hardware/Sim Split at Bind Time

`RobotBindings` selects between `FuelShot` and `FuelShotSim` using `Robot.isReal()` when `bind()` is called. This means simulation gets the projectile animation instead of real indexer commands, without any conditional logic inside the commands themselves.

### Debounced Auto-Fire

The 0.25 s falling-edge debounce on `shootReady()` prevents the auto-fire command from stopping and restarting during brief tracking interruptions (e.g., vibration, momentary sensor noise). The robot keeps feeding game pieces through transient mis-aims rather than stopping mid-cycle.
