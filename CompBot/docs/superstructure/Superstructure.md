# Superstructure

## Overview

`Superstructure` is the top-level robot object. It constructs every subsystem, performs the hardware/sim IO selection for each one, creates the `StateManager`, and exposes the command-building and drive-binding API used by the binding layer. It is the single point of entry for all robot behavior.

---

## `Subsystems` Record

An inner record that bundles all seven subsystem references into one object passed throughout the system.

```java
public record Subsystems(
    Drivetrain drivetrain,
    Turret turret,
    Shooter shooter,
    Indexer indexer,
    Intake intake,
    Climber climber,
    Led led)
```

---

## Constructor

Constructs all subsystems, selecting hardware or sim IO based on `RobotIdentifier.id()`. Three robot variants are supported:

| Robot | Drivetrain constants | Other IO |
|---|---|---|
| `CompBot` | `CompBotTunerConstants.createDrivetrain()` | Hardware IOs for all subsystems (Climber uses `ClimberIOSim` pending hardware integration) |
| `SimBot` | `CompBotTunerConstants.createDrivetrain()` | Sim IOs for all non-drivetrain subsystems |
| `TestBot` | `TestBotTunerConstants.createDrivetrain()` | Sim IOs for all non-drivetrain subsystems |

After constructing all subsystems, packages them into a `Subsystems` record and passes it to a new `StateManager`.

---

## API

### `bindDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot, Supplier<TeleopDriveMode> mode)`

Sets `Drivetrain.teleopDrive(...)` as the drivetrain's default command. Called by the driver bindings. The `mode` supplier controls whether the robot drives field-relative, robot-relative, or in the slow scoring mode. When `state.shooting(ShootMode.Scoring)` is active, the mode is automatically overridden to `SlowFieldRelativeSpin` regardless of what the driver selects.

### `build(CommandBuilder builder)` → `Command`

The primary command construction method. Calls `buildWithoutProxy(builder)` and wraps the result in `.asProxy()`. The proxy ensures that subsystem requirements are isolated from the outer command group — important for autonomous routines where commands from different groups need to run without conflicting requirement checks.

### `buildWithoutProxy(CommandBuilder builder)` → `Command`

Builds the command without proxying. Names it using the builder's simple class name for SmartDashboard visibility. Used for default commands, which must explicitly declare their subsystem requirements. Should be used sparingly.

### `createAprilTagVisionHandler()` → `AprilTagVisionHandler`

Factory method that constructs a vision handler with a reference back to this `Superstructure`, allowing vision updates to flow into the drivetrain's pose estimator. Configures four cameras (`cam1`–`cam4`) with their robot-relative transforms hardcoded here.

### `addPoseEstimate(TimestampedPoseEstimate estimate)`

Delegates to `drivetrain.addPoseEstimate(estimate)`. The vision handler calls this each time a camera measurement arrives.

### `periodic()`

Called each robot loop from `Robot.robotPeriodic()`:
1. `state.update()` — recalculates shoot mode, resets aim parameter cache, recomputes aim params
2. `subsystems.led().update(state)` — updates LED strip state based on current robot state

---

## Notes

- `state` is `public final` — binders and other robot-level code access `StateManager` triggers and methods directly via `superstructure.state`.
- `subsystems` is `private` — external code interacts with subsystems only through the `Superstructure` API or by receiving them through `CommandBuilder.build(subsystems, state)`.
