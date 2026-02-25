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
            │     └─ Climber
            │
            └─► StateManager  (public)
                  ├─ aimParams()           ← lazy-cached current aim solution
                  ├─ predictedAimParams()  ← lazy-cached predicted aim solution
                  ├─ aimTarget()           ← hub or feed target (alliance-aware)
                  ├─ robotPose()           ← current estimated pose
                  ├─ robotVelocity()       ← field-relative velocity
                  ├─ turretPose()          ← turret 3D field pose
                  ├─ shootReady()  ─Trigger─► RobotBindings auto-fire
                  └─ climbed()    ─Trigger─► RobotBindings teleop re-home
```

---

## Subsystem Construction & IO Selection

The `Superstructure` constructor is the **only place** where hardware vs. sim IO is selected. `Robot.isReal()` is evaluated once per subsystem at startup:

```
Robot.isReal() == true   →   *IOHardware (real TalonFX, CANcoder, CANrange, etc.)
Robot.isReal() == false  →   *IOSim      (DCMotorSim, state stores, lag models)
```

All subsystem code above the IO layer never knows which implementation it is running against.

---

## Command Building

All commands are built through `Superstructure.build(CommandBuilder)`:

```
superstructure.build(new FuelShot())
       │
       ├─ builder.build(subsystems, state)   ← constructs the command
       ├─ .withName("FuelShot")              ← names it for SmartDashboard
       └─ .asProxy()                         ← isolates subsystem requirements
```

The `.asProxy()` wrapper is critical for autonomous — it prevents subsystem requirement conflicts between commands scheduled from different autonomous groups running concurrently.

`buildWithoutProxy()` exists for default commands only, which must explicitly own their subsystem requirements to prevent other commands from interrupting them.

---

## Aim Parameter Lifecycle

`StateManager` uses a **lazy-evaluation cache** so the `AimStrategy` solver runs at most once per loop cycle regardless of how many consumers read aim parameters:

```
Loop start
    │
    └─► StateManager.periodic()
              params = Unchecked
              predictedParams = Unchecked

Any consumer calls aimParams():
    First call  → solve → cache
    Later calls → return cached value

Any consumer calls predictedAimParams():
    First call  → solve (using predicted pose + velocity) → cache
    Later calls → return cached value

Loop end → cycle repeats
```

**Active strategy:** `PhysicsAim` with the following constraints (from `AimConstants`):

| Parameter | Value |
|---|---|
| Min pitch | 49.5° |
| Max pitch | 72.0° |
| Max output | 18 m/s |
| Min descent velocity | 2 m/s |
| Max descent velocity | 10 m/s |

---

## Target Selection

`StateManager.aimTarget()` automatically selects the scoring target based on field position:

```
Robot pose
    │
    ├─ inAllianceZone (x ≤ 182.11" from own wall)
    │       └─► hub()         (4.63, 4.01, 1.83 m — Blue, mirrored for Red)
    │
    └─ outside alliance zone
            └─► feedTarget()  (4.5, 2.0, 1.0 m — Blue, mirrored for Red)
```

Both targets are flipped automatically for Red alliance via `FieldUtils.allianceRelativeFlip()`.

---

## `shootReady` Trigger

The primary gate for releasing a game piece. All three conditions must be true simultaneously:

```
shootReady()
    ├─ turret.tracked(aimParams)     ← |yaw error| ≤ ±2°
    ├─ shooter.tracked(aimParams)    ← |velocity error| ≤ ±0.35 AND |pitch error| ≤ ±4°
    └─ params.isOk()                 ← AimStatus == Possible
```

In `RobotBindings`, this trigger has a 250 ms falling-edge debounce applied before gating the auto-fire command, preventing rapid stop/start on transient misalignments.
