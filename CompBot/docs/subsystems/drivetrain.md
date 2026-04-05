# Drivetrain Subsystem

## Overview

The Drivetrain subsystem controls the robot's swerve drive. Unlike the other subsystems, it does **not** follow the IO-layer pattern — it directly extends the CTRE-generated `TunerSwerveDrivetrain` class (from `CompBotTunerConstants`) and implements WPILib's `Subsystem` interface. All low-level swerve kinematics, odometry, and motor control are handled by the CTRE Phoenix 6 swerve library. This class adds teleop driving, vision integration, pose prediction, operator alliance perspective management, and SysId characterization on top.

---

## Files

| File | Role |
|---|---|
| `Drivetrain.java` | Subsystem — extends `TunerSwerveDrivetrain`, adds commands and utilities |

There are no `DrivetrainIO`, `DrivetrainConstants`, or hardware/sim split files. Constants live in the CTRE-generated `CompBotTunerConstants` (comp robot) or `TestBotTunerConstants` (test robot). `AutopilotConstants` holds autopilot PID tuning.

---

## Key Constants (defined inline)

| Constant | Value | Description |
|---|---|---|
| `kSimLoopPeriod` | 4 ms | Rate of the simulation notifier thread |
| `maxSpeed` | `kSpeedAt12Volts` in m/s | Max translational speed for teleop |
| `maxRotationalSpeed` | 2π rad/s | Max rotational rate for teleop |
| `kBlueAlliancePerspectiveRotation` | 0° | Forward direction for Blue alliance |
| `kRedAlliancePerspectiveRotation` | 180° | Forward direction for Red alliance |

---

## Architecture

```
Drivetrain
    └─ extends TunerSwerveDrivetrain  (CTRE-generated, handles modules/odometry/IMU)
           └─ extends SwerveDrivetrain (Phoenix 6 swerve library)
```

The CTRE swerve library manages all four swerve modules internally. `Drivetrain` wraps it with:
- Alliance-aware operator perspective
- Teleop drive command
- Vision measurement ingestion
- Pose prediction
- SysId characterization routines
- Telemetry logging

---

## Constructor

Accepts `SwerveDrivetrainConstants` and `SwerveModuleConstants` (varargs), passing them directly to the superclass.

- In simulation: starts a high-frequency `Notifier` thread (4 ms) to step the sim state using WPILib battery voltage.
- Registers `Valid Odometry`, `Robot Pose`, and `Time since last estimate` with `OnboardLogger` under `"Drivetrain"`.
- Calls `configurePathplanner()` to register the drivetrain with PathPlanner's `AutoBuilder` for autonomous path-following.

---

## `periodic()`

Each robot loop cycle:

1. **Operator perspective** — if the alliance has never been applied (or the robot is disabled), reads the DS alliance and calls `setOperatorPerspectiveForward()` with the appropriate rotation. This corrects field-relative drive if code restarts mid-match.
2. **State snapshot** — calls `getState()` to refresh the cached `SwerveDriveState`.
3. **Pose logging** — pushes current pose to `FieldManager`'s Field2d widget.

---

## Commands

### `teleopDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot, Supplier<TeleopDriveMode> modeSupplier)`

Field-centric swerve drive for teleop.

- Scales raw joystick inputs by `maxSpeed` and `maxRotationalSpeed`.
- Rotates the XY translation by `getOperatorForwardDirection()` for alliance-aware field-relative drive.
- **`TeleopDriveMode.FieldRelativeSpin`** — normal field-centric drive with spin control.
- **`TeleopDriveMode.SlowFieldRelativeSpin`** — same as above but at 30% translation and 50% rotation speed (used during scoring shots).
- **`TeleopDriveMode.RobotRelative`** — robot-centric drive, useful for precise alignment.
- If a drivetrain aim `override` is active (from `track()`), heading control is handed to the override's `AimParams.yaw` regardless of mode.

An overload `teleopDrive(vx, vy, vrot)` exists that defaults to `FieldRelativeSpin`.

### `rotate()`

Robot-centric rotation at 0.5π rad/s. Utility command for testing.

### `driveTo(Supplier<APTarget> target, Autopilot autopilot)`

Autonomous path-following using the `Autopilot` library. Runs until `autopilot.atTarget(robotPose, target)` is true, then stops the drivetrain (unless the target has a non-zero exit velocity).

### `track(Supplier<AimParams> params)`

While active, overrides the heading controller in `teleopDrive` to face the yaw from the provided `AimParams`. Cleared on command end. Used by `DrivetrainAim` command.

### `resetOdometry(Pose2d pose, boolean flip)`

Runs once to reset the pose estimator. If `flip` is true, mirrors the pose for Red alliance.

### SysId Commands

Three characterization routines are defined. The active one is selected by `m_sysIdRoutineToApply` (defaults to translation).

| Routine | Purpose | Dynamic Voltage |
|---|---|---|
| Translation | Find drive motor PID gains | 4 V |
| Steer | Find steer motor PID gains | 7 V |
| Rotation | Find heading controller gains | π rad/s² ramp, π rad/s step |

> Note: The rotation routine repurposes the SysId `Volts` unit to carry radians/second values, as SysId has no native angular rate type. The log labels document this.

`sysIdQuasistatic(Direction)` and `sysIdDynamic(Direction)` expose the active routine to be bound in `RobotContainer`.

---

## Pose & Velocity API

| Method | Returns | Description |
|---|---|---|
| `robotPose()` | `Pose2d` | Current estimated pose from odometry/vision fusion |
| `robotVelocity()` | `Transform2d` | Field-relative velocity (vx, vy, omega) as a Transform2d |
| `predictedRobotPose()` | `Pose2d` | Pose 2 loops in the future using `exp(twist)` |
| `predictedRobotVelocity()` | `Translation2d` | Velocity 2 loops in the future, rotated by ω×dt |
| `validOdemetry()` | `Trigger` | True when a vision update arrived within `kValidOdometryCutoff` seconds |

`predictedRobotPose()` and `predictedRobotVelocity()` are used by the aiming system to lead the target when the robot is moving. `validOdemetry()` is used by `StateManager.shootReady` to gate auto-fire on pose confidence.

---

## Vision Integration

### `addPoseEstimate(TimestampedPoseEstimate estimate)`

The primary entry point for vision updates. Called by the vision subsystem each time a new camera measurement arrives.

- Updates `lastOkayVisionUpdateTime` to the current FPGA timestamp (used by `validOdemetry()`).
- **Skipped entirely in simulation** — vision fusion is hardware-only to avoid corrupting sim odometry.
- Calls `addVisionMeasurement(pose, timestamp, stdDevs)` with per-estimate standard deviations.

### `addVisionMeasurement(...)` overrides

Both single-argument and std-dev-argument overrides are provided. They convert the incoming FPGA timestamp using `Utils.fpgaToCurrentTime()` before forwarding to the Phoenix 6 Kalman filter.

### `samplePoseAt(double timestampSeconds)`

Returns the pose from the odometry buffer at a given timestamp (used for latency compensation). Also applies `fpgaToCurrentTime` conversion.

---

## Simulation

A `Notifier` runs `updateSimState(deltaTime, batteryVoltage)` at **4 ms intervals** (250 Hz), independent of the main robot loop (20 ms / 50 Hz). This higher rate keeps the sim PID controllers stable, as swerve module control loops are sensitive to update rate.
