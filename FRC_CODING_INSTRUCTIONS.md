# FRC Robot Programming Instructions

Based on Team 3414's FRC Programming Reference

## Table of Contents
1. [Core Concepts](#core-concepts)
2. [Subsystem Design](#subsystem-design)
3. [Command Structure](#command-structure)
4. [Command Factories](#command-factories)
5. [Command Compositions](#command-compositions)
6. [Command Decorators](#command-decorators)
7. [Triggers and Bindings](#triggers-and-bindings)
8. [Advanced Patterns](#advanced-patterns)
   - [IO Layer Separation](#io-layer-separation)
   - [Non-Subsystem Code](#non-subsystem-code-vision-utilities-etc)
   - [Generated Code](#generated-code-ctre-etc)
   - [Thread Safety](#thread-safety-notes)

---

## Core Concepts

### WPILib and Command-Based Programming
- WPILib is a library that supplies robot-focused features
- Use **command-based programming** as the primary architectural approach
- Structure code using subsystems (hardware abstraction) and commands (behaviors)

### Declarative vs Imperative Programming
- **Declarative**: Express *what* should happen (e.g., `shooter.shoot()`)
- **Imperative**: Focus on *how* to accomplish it (e.g., `motor.setVoltage(12)`)
- Always prefer declarative approaches in your subsystem public APIs

---

## Subsystem Design

### Basic Structure
Subsystems extend `SubsystemBase` and represent functional features of the robot, not just hardware components.

```java
public class Shooter extends SubsystemBase {
    private final WPI_TalonFX motor; // Hardware is private

    public Shooter() {
        motor = new WPI_TalonFX(1);
    }

    @Override
    public void periodic() {
        // Called every 20ms - use for sensor reading and state updates
        // NEVER write commands here to avoid conflicts
    }
}
```

### Key Principles

#### 1. Hardware Protection
- **Always** declare all hardware as `private`
- Never expose hardware objects directly
- Provide meaningful getter methods instead

```java
// BAD
public CANSparkMax getMotor() { return motor; }

// GOOD
public boolean hasGamepiece() {
    return beamBreak.get();
}
```

#### 2. Controlled States
- Avoid methods that accept arbitrary values
- Create specific public methods for valid states

```java
// BAD
public void setVoltage(double voltage) { ... }
public void go(double position) { ... }

// GOOD
public void stow() { ... }
public void deploy() { ... }
public void shoot() { ... }
```

#### 3. Use Enums for Setpoints
Prevents invalid inputs and improves clarity:

```java
public enum ElevatorState {
    L1(10.0),
    L2(20.0),
    L3(30.0);

    public final double position;
    ElevatorState(double position) {
        this.position = position;
    }
}

public Command goTo(ElevatorState state) {
    return run(() -> setPosition(state.position));
}
```

#### 4. Return Triggers, Not Booleans (For Command-Based Subsystems)
For **boolean states** in command-based subsystems, return Triggers instead of booleans. Triggers update with subsystem state and can be directly used for command bindings.

**Note**: This principle applies to boolean conditions in subsystems, not to data types like `Pose2d` or `Transform2d`, which should be returned directly.

```java
// BAD - for boolean states in subsystems
public boolean atSetpoint() {
    return Math.abs(target - getPosition()) < tolerance;
}

// GOOD - for boolean states in subsystems
public Trigger atSetpoint() {
    return new Trigger(() ->
        Math.abs(target - getPosition()) < tolerance
    );
}

// GOOD - non-boolean data should be returned directly
public Pose2d robotPose() {
    return currentPose;
}
```

#### 5. Return Commands, Not Void Methods
Commands ensure proper completion and scheduler integration:

```java
// BAD
public void setStow() { target = STOW_POSITION; }

// GOOD
public Command stow() {
    return runOnce(() -> target = STOW_POSITION);
}
```

---

## Command Structure

### Lifecycle Methods
Commands override four key methods:

```java
public class ExampleCommand extends Command {
    @Override
    public void initialize() {
        // Setup - called once when command starts
        // Establish known states for subsystems
    }

    @Override
    public void execute() {
        // Runs repeatedly at 50Hz (every 20ms)
        // Process sensor readings and apply outputs
    }

    @Override
    public boolean isFinished() {
        // Return true when command should end
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        // Cleanup - called once when ending
        // interrupted = true if interrupted, false if completed naturally
    }
}
```

### Execution Flow
1. `initialize()` - Run once
2. `execute()` - Repeated at 50Hz
3. `isFinished()` - Check after each execute
4. `end(interrupted)` - Run once when stopping

---

## Command Factories

Static helper methods that create commands without subclassing. More concise and declarative.

### Common Factories

#### `run(Runnable action)`
Executes repeatedly without ending:
```java
Commands.run(() -> System.out.println("Running..."))
```

#### `runOnce(Runnable action)`
Executes once then completes:
```java
Commands.runOnce(() -> intake.deploy())
```

#### `idle()`
Does nothing and never finishes (blocks subsystem):
```java
Commands.idle()
```

#### `none()`
Does nothing and ends immediately:
```java
Commands.none()
```

#### `waitUntil(BooleanSupplier condition)`
Polls condition each cycle until true:
```java
Commands.waitUntil(intake::atSetpoint)
```

#### `either(Command onTrue, Command onFalse, BooleanSupplier condition)`
Selects command based on runtime condition:
```java
Commands.either(
    autoCommand,
    teleopCommand,
    () -> DriverStation.isAutonomous()
)
```

#### `select(Map<Object, Command> commands, Supplier<Object> selector)`
Chooses from multiple commands using a key:
```java
Commands.select(
    Map.of(
        Mode.INTAKE, intakeCommand,
        Mode.SHOOT, shootCommand
    ),
    this::getCurrentMode
)
```

### Subsystem Requirements

#### Direct Passing
```java
Commands.waitUntil(intake::atSetpoint, intake)
```

#### SubsystemBase Shorthand
Subsystems provide the same factories that auto-require themselves:
```java
// These are equivalent:
Commands.run(intake::update, intake)
intake.run(intake::update)
```

---

## Command Compositions

Compositions group multiple commands and execute them as a unit. They are themselves commands and can be nested.

### Sequential Group
Executes commands one after another:
```java
Commands.sequence(
    intake.deploy(),
    intake.runIntake(),
    intake.stow()
)
```

### Parallel Group
Runs commands simultaneously (no shared requirements):
```java
Commands.parallel(
    arm.goToPosition(),
    shooter.spinUp()
)
```

### Race Group
Runs in parallel, ends when ANY command finishes (cancels others):
```java
Commands.race(
    intake.runIntake(),
    Commands.waitSeconds(2.0)
)
```

### Deadline Group
Runs in parallel, ends when the FIRST command finishes:
```java
Commands.deadline(
    Commands.waitSeconds(2.0),  // Deadline
    intake.runIntake(),          // Interrupted when deadline ends
    shooter.idle()
)
```

### Alternative Syntax

| Factory | Command Method |
|---------|----------------|
| `Commands.sequence(a, b)` | `a.andThen(b)` |
| `Commands.parallel(a, b)` | `a.alongWith(b)` |
| `Commands.race(a, b)` | `a.raceWith(b)` |
| `Commands.deadline(a, b)` | `a.deadlineFor(b)` |

**Recommendation**: Use static `Commands` factories for better maintainability and scalability.

---

## Command Decorators

Built-in methods that modify command behavior without changing core execution.

### Conditional Execution

#### `unless(BooleanSupplier condition)`
Prevents execution if condition is true (checked at scheduling):
```java
shootCommand.unless(() -> !hasGamepiece())
```

#### `onlyIf(BooleanSupplier condition)`
Executes only if condition is true (checked at scheduling):
```java
autoCommand.onlyIf(() -> DriverStation.isAutonomous())
```

### Continuous Conditions

#### `until(BooleanSupplier condition)`
Interrupts if condition becomes true during execution:
```java
intakeCommand.until(() -> hasGamepiece())
```

#### `onlyWhile(BooleanSupplier condition)`
Runs only while condition remains true:
```java
driveCommand.onlyWhile(() -> DriverStation.isTeleop())
```

### Cleanup Handlers

#### `finallyDo(Runnable end)`
Executes when command ends (regardless of interruption):
```java
command.finallyDo(() -> System.out.println("Done!"))
```

#### `finallyDo(BooleanConsumer end)`
Receives interruption status:
```java
command.finallyDo((interrupted) -> {
    if (interrupted) {
        System.out.println("Interrupted");
    } else {
        System.out.println("Completed");
    }
})
```

**Important**: Subsystem requirements are reserved even if decorators prevent execution.

---

## Triggers and Bindings

### Creating Triggers

Triggers wrap `BooleanSupplier` for powerful conditionals:
```java
Trigger armReady = new Trigger(arm::isAtSetpoint);
Trigger hasPiece = new Trigger(intake::holdingPiece);
```

### Logical Operations
```java
Trigger both = armReady.and(hasPiece);
Trigger either = armReady.or(hasPiece);
Trigger neither = either.negate();
```

### Binding Commands

| Method | Behavior |
|--------|----------|
| `onTrue(Command)` | Schedules when trigger goes false → true |
| `onFalse(Command)` | Schedules when trigger goes true → false |
| `whileTrue(Command)` | Schedules on false → true, interrupts if returns to false |
| `whileFalse(Command)` | Schedules on true → false, interrupts if returns to true |
| `onChange(Command)` | Schedules on any state change |

### Controller Bindings

Use command-specific controller classes:
```java
CommandPS5Controller controller = new CommandPS5Controller(0);

controller.square().onTrue(pivot.stow());
controller.circle().whileTrue(intake.runIntake());
controller.cross().onTrue(shooter.shoot());
controller.triangle().toggleOnTrue(climber.deploy());
```

---

## Advanced Patterns

### IO Layer Separation

Separate hardware from subsystem logic for testability and simulation:

```java
// IO Interface
public interface ShooterIO {
    public static class Inputs {
        public double velocityRPM;
        public double currentAmps;
        public double temperatureC;
    }

    void updateInputs(Inputs inputs);
    void setVoltage(double volts);
}

// Hardware Implementation
public class ShooterIOTalonFX implements ShooterIO {
    private final WPI_TalonFX motor;

    @Override
    public void updateInputs(Inputs inputs) {
        inputs.velocityRPM = motor.getSelectedSensorVelocity();
        inputs.currentAmps = motor.getStatorCurrent();
        inputs.temperatureC = motor.getTemperature();
    }

    @Override
    public void setVoltage(double volts) {
        motor.setVoltage(volts);
    }
}

// Simulation Implementation
public class ShooterIOSim implements ShooterIO {
    // Simulation logic...
}

// Subsystem
public class Shooter extends SubsystemBase {
    private final ShooterIO io;
    private final ShooterIO.Inputs inputs = new ShooterIO.Inputs();

    public Shooter(ShooterIO io) {
        this.io = io;
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);
        // Use inputs for logic
    }
}
```

### Benefits of IO Pattern
- Seamless switching between hardware and simulation
- Consistent state (all sensors updated once per cycle)
- Easy testing without hardware
- Multiple implementations of same interface

### Non-Subsystem Code (Vision, Utilities, etc.)

Not all robot code follows the command-based subsystem pattern. Some components intentionally operate outside this paradigm:

#### Vision Processing
Vision classes are **not** subsystems because:
- Subsystems are tightly coupled with the command requirements system
- Vision code has no need for command "requirements" - nothing should exclusively "own" vision processing
- Vision state shouldn't be used to trigger commands directly (that's what Triggers in subsystems are for)
- Vision often runs on separate threads with different timing requirements

```java
// Vision classes implement Runnable or use Notifier, not SubsystemBase
public class SingleInputPoseEstimator implements Runnable {
    // Returns boolean directly - this is NOT a subsystem
    public boolean isConnected() {
        return inputs.connected;
    }
}
```

#### Access Modifiers for Architecture
Sometimes classes need to be public for architectural reasons:

```java
// Subsystems record is public so CommandBuilder implementations can access it
public record Subsystems(Drivetrain drivetrain, Shooter shooter) {}

public interface CommandBuilder {
    // Needs access to Subsystems
    Command build(Subsystems subsystems, StateManager state);
}
```

### Generated Code (CTRE, etc.)

When using vendor-generated code (e.g., CTRE's TunerSwerveDrivetrain):
- Follow the vendor's conventions for that code
- Don't add `super.periodic()` calls unless the generated code includes them
- The vendor knows their library's requirements

### Thread Safety Notes

The WPILib command scheduler runs everything in a single thread. This means:
- Non-final fields reassigned in `periodic()` don't cause race conditions
- You don't need to worry about synchronization for state accessed only by commands and subsystems
- Vision code running on separate threads (via `Notifier`) should handle its own thread safety

---

## Best Practices Summary

**For Command-Based Subsystems:**
1. **Subsystems**: Represent features, not hardware. Keep hardware private.
2. **API Design**: Use enums for setpoints, return Triggers for boolean states, return Commands instead of void methods.
3. **Commands**: Use factories over subclassing when possible.
4. **Compositions**: Prefer static `Commands` factories for clarity.
5. **Decorators**: Add conditional logic without modifying core commands.
6. **Triggers**: Bind commands to buttons and conditions declaratively.
7. **Architecture**: Use IO layer pattern for testability and simulation.
8. **Periodic**: Never write commands in `periodic()` methods.
9. **Declarative**: Always prefer "what" over "how" in your APIs.
10. **Protection**: Prevent invalid states through careful API design.

**For Non-Subsystem Code (Vision, Utilities):**
11. **Vision classes** are intentionally not subsystems - they don't need command requirements.
12. **Return types**: Use appropriate types directly (booleans for vision, Pose2d for state) - Triggers are for command bindings.
13. **Generated code**: Follow vendor conventions; don't override patterns the vendor established.

---

## Repository Contents

### Simulator Configuration Files
Simulator configuration files (such as `simgui-ds.json`, `simgui.json`, and similar) **should be committed** to the repository. These files:
- Store joystick mappings and driver station configurations for simulation
- Ensure consistent simulation experience across all team members
- Are necessary for the robot simulation to function properly with the expected controls

Do not add these files to `.gitignore`.

---

## Additional Resources

- Full reference: https://therekrab.github.io/frc-programming-reference/
- WPILib documentation: https://docs.wpilib.org/

---

## Robot Design Assumptions

This section documents the design assumptions for all subsystems and automations on the 2026 robot.

### Subsystem Assumptions

#### Drivetrain
- Swerve drive configuration
- 8 TalonFX motors (2 per module)
- 4 absolute encoders for steering
- Accurate pose estimation capability
- Gyro for reading the azimuth of robot
- Accurate drive-to-pose within reasonable tolerance

#### Intake
- Capable of picking up the ball (fuel)
- Runs at a non-bottlenecking speed; fast
- Trackable change in current for jam detection
- Not passive (actively controlled)
- Can read motor velocity
- Ability to detect gamepieces in the mechanism
- Can eject pieces that have fully passed through the subsystem

#### Climber
- Must support the robot's weight
- Capable of climbing all three levels
- Capability to disengage from L1

#### Shooter
- Controllable pitch angle of fuel
- Controlling the speed of the flywheel
- Accurate control over gamepiece final location
- Can read motor position/velocities to determine if shooting is ready

#### Indexer
- Moves the fuel from the hopper into the shooter
- Not passive (actively controlled)
- Has eject capability

#### Turret
- Limited effective range of motion: not all angles are possible/usable
- Knows its angular position
- Has some homing detection system

### Automation Assumptions

#### Live Tracking
- **Subsystems**: Turret, Shooter
- **Initial State**: Either in alliance zone with active hub, or outside alliance zone
- **Steps**: `turret.track()` and `shooter.prepare()` run in parallel

#### Auto Align and Climb
- **Subsystems**: Drivetrain, Climber
- **Initial State**: Close to tower, not yet climbed, human triggers action, human selects climb spot
- **Steps**: Align with `drivetrain.driveToPoint()`, prepare with `climber.prepare()`, climb with `climber.climb()`

#### Auto Shoot
- **Subsystems**: Shooter, Turret
- **Initial State**: Can check for good launch angles/rotations, in alliance zone (not BUMP or TRENCH) or human-triggered, shooter and turret are ready
- **Steps**: `shooter.shoot()`

#### Auto Intake
- **Subsystems**: Drivetrain, Intake
- **Initial State**: Vision detects fuel nearby, human-triggered
- **Steps**: Find fuel using vision, drive to fuel with calculated velocity, `intake.intake()` in parallel

#### Auto Corral
- **Subsystems**: Intake, Drivetrain, Indexer
- **Initial State**: Have fuel, close to corral station, triggered by human button
- **Steps**: `drivetrain.driveToPose(corralStation)`, then `intake.reverse()` and `indexer.reverse()`

#### Auto Unjam
- **Subsystems**: Intake
- **Initial State**: Intake reads possible jam error mode
- **Steps**: `intake.reverse()`
