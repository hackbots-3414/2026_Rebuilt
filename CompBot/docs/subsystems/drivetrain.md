# Drivetrain Subsystem

## Overview

The Drivetrain subsystem controls the robot's swerve drive. Unlike the other subsystems, it does **not** follow the IO-layer pattern — it directly extends the CTRE-generated `TunerSwerveDrivetrain` class (from `TestBotTunerConstants`) and implements WPILib's `Subsystem` interface. All low-level swerve kinematics, odometry, and motor control are handled by the CTRE Phoenix 6 swerve library. This class adds teleop driving, vision integration, pose prediction, operator alliance perspective management, and SysId characterization on top.

---

## Files

| File | Role |
|---|---|
| `Drivetrain.java` | Subsystem — extends `TunerSwerveDrivetrain`, adds commands and utilities |

There are no `DrivetrainIO`, `DrivetrainConstants`, or hardware/sim split files. Constants live in the CTRE-generated `TestBotTunerConstants`.

---

## Key Constants (defined inline)

| Constant | Value | Description |
|---|---|---|
| `kSimLoopPeriod` | 4 ms | Rate of the simulation notifier thread |
| `maxSpeed` | 100% of `kSpeedAt12Volts` | Max translational speed for teleop |
| `maxAngularRate` | 0.75 rot/s | Max rotational rate for teleop |
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
- Creates a `StructLogEntry<Pose2d>` for structured pose logging via `DataLogManager`.
- Registers `hasReceivedVisionUpdate` flag with `OnboardLogger` under `"Drivetrain"`.

---

## `periodic()`

Each robot loop cycle:

1. **Operator perspective** — if the alliance has never been applied (or the robot is disabled), reads the DS alliance and calls `setOperatorPerspectiveForward()` with the appropriate rotation. This corrects field-relative drive if code restarts mid-match.
2. **State snapshot** — calls `getState()` to refresh the cached `SwerveDriveState`.
3. **Pose logging** — updates the `StructLogEntry` and pushes pose to `FieldManager`'s Field2d widget.
4. **Vision flag reset** — resets `hasReceivedVisionUpdate` to `false` each cycle (set to `true` by `addPoseEstimate()` if a vision update arrived).

---

## Commands

### `teleopDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot)`

Field-centric swerve drive for teleop.

- Scales raw joystick inputs by `maxSpeed` and `maxAngularRate`.
- Rotates the XY translation by `getOperatorForwardDirection()` so the inputs are truly operator-perspective-relative rather than raw field-relative. This handles the Blue/Red alliance flip cleanly.
- Uses `SwerveRequest.FieldCentric` with `ForwardPerspectiveValue.BlueAlliance` and `DriveRequestType.Velocity`.

### `rotate()`

Robot-centric rotation at π/2 rad/s. Utility command (likely for testing or alignment).

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
| `predictedRobotPose()` | `Pose2d` | Pose one loop period in the future using `exp(twist)` |
| `predictedRobotVelocity()` | `Translation2d` | Velocity one loop period in the future, rotated by ω×dt |

`predictedRobotPose()` and `predictedRobotVelocity()` are used by the aiming system to lead the target when the robot is moving.

---

## Vision Integration

### `addPoseEstimate(TimestampedPoseEstimate estimate)`

The primary entry point for vision updates. Called by the vision subsystem each time a new camera measurement arrives.

- Sets `hasReceivedVisionUpdate = true`.
- **Skipped entirely in simulation** — vision fusion is hardware-only to avoid corrupting sim odometry.
- Calls `addVisionMeasurement(pose, timestamp, stdDevs)` with per-estimate standard deviations.

### `addVisionMeasurement(...)` overrides

Both single-argument and std-dev-argument overrides are provided. They convert the incoming FPGA timestamp using `Utils.fpgaToCurrentTime()` before forwarding to the Phoenix 6 Kalman filter.

### `samplePoseAt(double timestampSeconds)`

Returns the pose from the odometry buffer at a given timestamp (used for latency compensation). Also applies `fpgaToCurrentTime` conversion.

---

## Simulation

A `Notifier` runs `updateSimState(deltaTime, batteryVoltage)` at **4 ms intervals** (250 Hz), independent of the main robot loop (20 ms / 50 Hz). This higher rate keeps the sim PID controllers stable, as swerve module control loops are sensitive to update rate.
