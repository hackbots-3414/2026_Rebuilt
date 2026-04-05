# Superstructure — Overview

## Purpose

The superstructure layer is the backbone of the robot. It sits between hardware (subsystems) and behavior (commands + bindings), providing a single construction point for all subsystems, a shared state hub, and a clean API for building commands. Nothing outside this layer needs to know which IO implementation is in use or how aim parameters are computed.

---

## Files

| File | Role |
|---|---|
| `Superstructure.java` | Constructs all subsystems, manages hardware/sim selection, exposes command building API |
| `StateManager.java` | Robot-wide shared state — pose, aim params, target selection, composite triggers |

---

## Architecture

```
Robot.java
    │
    └─► Superstructure  (constructed once in RobotContainer)
            │
            ├─► Subsystems record
            │     ├─ Drivetrain
            │     ├─ Turret
            │     ├─ Shooter
            │     ├─ Indexer
            │     ├─ Intake
            │     ├─ Climber
            │     └─ Led
            │
            └─► StateManager  (public)
                  ├─ aimParams()           ← lazy-cached aim solution (mode-aware)
                  ├─ robotPose()           ← current estimated pose
                  ├─ robotVelocity()       ← field-relative velocity
                  ├─ turretPose()          ← turret 3D field pose
                  ├─ shootReady    ─Trigger─► RobotBindings auto-fire
                  ├─ climbing()   ─Trigger─► RobotBindings teleop re-home
                  └─ intaking()   ─Trigger─► RobotBindings agitate gate
```

---

## Subsystem Construction & IO Selection

The `Superstructure` constructor is the **only place** where hardware vs. sim IO is selected. `RobotIdentifier.id()` is evaluated once at startup and returns one of three robot variants:

```
CompBot  →   Hardware IOs (real TalonFX, CANcoder, CANrange, etc.)
SimBot   →   Sim IOs      (DCMotorSim, state stores, lag models)
TestBot  →   Sim IOs for all non-drivetrain subsystems; real TestBot drivetrain
```

All subsystem code above the IO layer never knows which implementation it is running against.

---

## Command Building

All commands are built through `Superstructure.build(CommandBuilder)`:

```
superstructure.build(new RunIndex())
       │
       ├─ builder.build(subsystems, state)   ← constructs the command
       ├─ .withName("RunIndex")              ← names it for SmartDashboard
       └─ .asProxy()                         ← isolates subsystem requirements
```

The `.asProxy()` wrapper is critical for autonomous — it prevents subsystem requirement conflicts between commands scheduled from different autonomous groups running concurrently.

`buildWithoutProxy()` exists for default commands only, which must explicitly own their subsystem requirements to prevent other commands from interrupting them.

---

## Aim Parameter Lifecycle

`StateManager` uses a **lazy-evaluation cache** and **shoot mode selection** to determine aim parameters each cycle:

```
Loop start
    │
    └─► StateManager.update()
              params = Unchecked
              call aimParams() → solve based on wantedShootMode → cache
              recalculate wantedShootMode for next cycle

Any consumer calls aimParams() this cycle:
    params.status != Unchecked → return cached value (no re-solve)
```

**Active strategy depends on `ShootMode`:**

| Mode | Target | Strategy |
|---|---|---|
| `Scoring` | `FieldUtils.hub()` | `AimConstants.kScoringAim` (`ToFAim` with `scoringMeasurements`) |
| `Feeding` | `FieldUtils.feedTarget(pose)` | `AimConstants.kFeedingAim` (`ToFAim` with `feedingMeasurements`) |
| `Donut` | — | Returns `AimParams.impossible()` immediately |

---

## `shootReady` Trigger

The primary gate for releasing a game piece. All four conditions must be true simultaneously:

```
shootReady
    ├─ params.isOk()                               ← AimStatus == Possible
    ├─ turret.tracked(aimParams)  (0.1s fall debounce)
    ├─ shooter.tracked(aimParams) (1.5s fall debounce)
    └─ validOdometry.or(!FORCE_ODOMETRY)
```

The 1.5 s falling debounce on the shooter prevents auto-fire from cycling on/off during momentary speed dips from drivetrain vibration.

---

## `shootReady` Trigger

`shootReady` is a public `Trigger` field on `StateManager` (not a method). All four conditions must be true simultaneously:

```
shootReady
    ├─ params.isOk()                               ← AimStatus == Possible
    ├─ turret.tracked(aimParams)  (0.1s fall)      ← |yaw error| ≤ deltaYaw
    ├─ shooter.tracked(aimParams) (1.5s fall)      ← |velocity error| ≤ deltaOutput AND |pitch error| ≤ deltaPitch
    └─ validOdometry.or(!FORCE_ODOMETRY)           ← drivetrain has recent vision update
```

The debounce is built into `StateManager.initShootReady()` — `RobotBindings` uses `shootReady` directly without adding additional debounce.
