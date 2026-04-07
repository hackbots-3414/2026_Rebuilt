package frc.robot.subsystems.drivetrain;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.SwerveRequest.ForwardPerspectiveValue;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.therekrab.autopilot.APTarget;
import com.therekrab.autopilot.Autopilot;
import com.therekrab.autopilot.Autopilot.APResult;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.FieldManager;
import frc.robot.Robot;
import frc.robot.aiming.AimParams;
import frc.robot.generated.CompBotTunerConstants;
import frc.robot.generated.CompBotTunerConstants.TunerSwerveDrivetrain;
import frc.robot.subsystems.drivetrain.AutopilotConstants.HeadingGains;
import frc.robot.subsystems.turret.TurretConstants;
import frc.robot.util.FieldUtils;
import frc.robot.util.OnboardLogger;
import frc.robot.util.StatusSignalUtil;
import frc.robot.vision.localization.LocalizationConstants;
import frc.robot.vision.localization.TimestampedPoseEstimate;

public class Drivetrain extends TunerSwerveDrivetrain implements Subsystem {
  private static final double kSimLoopPeriod = 0.004; // 4 ms
  private Notifier simNotifier = null;
  private double lastSimTime;

  private final double LOOKAHEAD = 2 * Robot.kDefaultPeriod;

  private static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
  private static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

  /* Keep track if we've ever applied the operator perspective before or not */
  private boolean hasAppliedOperatorPerspective = false;

  private double maxSpeed = CompBotTunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
  private double maxRotationalSpeed = 2.0 * Math.PI; // radians per second

  private Pose2d memorySpot = Pose2d.kZero;

  public enum TeleopDriveMode {
    /**
     * Drive the robot with a field-relative control for translation and spin control (i.e. control
     * over how fast we rotate)
     */
    FieldRelativeSpin,
    /** Drive the robot slower than FieldRelativeSpin */
    SlowFieldRelativeSpin,
    /** Drive robot relative. */
    RobotRelative,
  }

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
      .withForwardPerspective(ForwardPerspectiveValue.BlueAlliance)
      .withDriveRequestType(DriveRequestType.Velocity);

  private final SwerveRequest.RobotCentric robotCentricDrive = new SwerveRequest.RobotCentric()
      .withDriveRequestType(DriveRequestType.Velocity);

  private final SwerveRequest.ApplyRobotSpeeds robotSpeeds = new SwerveRequest.ApplyRobotSpeeds()
      .withDriveRequestType(DriveRequestType.Velocity);

  private final SwerveRequest.FieldCentricFacingAngle autopilotControl =
      new SwerveRequest.FieldCentricFacingAngle()
          .withForwardPerspective(ForwardPerspectiveValue.BlueAlliance)
          .withDriveRequestType(DriveRequestType.Velocity)
          .withHeadingPID(HeadingGains.kP, HeadingGains.kI, HeadingGains.kD);

  private final SwerveRequest.FieldCentricFacingAngle drivetrainAim =
      new SwerveRequest.FieldCentricFacingAngle()
          .withForwardPerspective(ForwardPerspectiveValue.BlueAlliance)
          .withDriveRequestType(DriveRequestType.Velocity)
          .withHeadingPID(HeadingGains.kP, HeadingGains.kI, HeadingGains.kD)
          .withCenterOfRotation(TurretConstants.kOffset.getTranslation().toTranslation2d());

  private SwerveDriveState state;

  private double lastOkayVisionUpdateTime;

  /* Swerve requests to apply during SysId characterization */
  private final SwerveRequest.SysIdSwerveTranslation translationCharacterization =
      new SwerveRequest.SysIdSwerveTranslation();
  private final SwerveRequest.SysIdSwerveSteerGains steerCharacterization =
      new SwerveRequest.SysIdSwerveSteerGains();
  private final SwerveRequest.SysIdSwerveRotation rotationCharacterization =
      new SwerveRequest.SysIdSwerveRotation();

  /*
   * SysId routine for characterizing translation. This is used to find PID gains for the drive
   * motors.
   */
  private final SysIdRoutine sysIdRoutineTranslation = new SysIdRoutine(
      new SysIdRoutine.Config(
          null, // Use default ramp rate (1 V/s)
          Volts.of(4), // Reduce dynamic step voltage to 4 V to prevent brownout
          null, // Use default timeout (10 s)
          // Log state with SignalLogger class
          state -> SignalLogger.writeString("SysIdTranslation_State", state.toString())),
      new SysIdRoutine.Mechanism(
          output -> setControl(translationCharacterization.withVolts(output)),
          null,
          this));

  /*
   * SysId routine for characterizing steer. This is used to find PID gains for the steer motors.
   */
  private final SysIdRoutine sysIdRoutineSteer = new SysIdRoutine(
      new SysIdRoutine.Config(
          null, // Use default ramp rate (1 V/s)
          Volts.of(7), // Use dynamic voltage of 7 V
          null, // Use default timeout (10 s)
          // Log state with SignalLogger class
          state -> SignalLogger.writeString("SysIdSteer_State", state.toString())),
      new SysIdRoutine.Mechanism(
          volts -> setControl(steerCharacterization.withVolts(volts)),
          null,
          this));

  /*
   * SysId routine for characterizing rotation. This is used to find PID gains for the
   * FieldCentricFacingAngle HeadingController. See the documentation of
   * SwerveRequest.SysIdSwerveRotation for info on importing the log to SysId.
   */
  @SuppressWarnings("unused")
  private final SysIdRoutine sysIdRoutineRotation = new SysIdRoutine(
      new SysIdRoutine.Config(
          /*
           * This is in radians per second^2, but SysId only supports "volts per second"
           */
          Volts.of(Math.PI / 6).per(Second),
          /* This is in radians per second, but SysId only supports "volts" */
          Volts.of(Math.PI),
          null, // Use default timeout (10 s)
          // Log state with SignalLogger class
          state -> SignalLogger.writeString("SysIdRotation_State", state.toString())),
      new SysIdRoutine.Mechanism(
          output -> {
            /* output is actually radians per second, but SysId only supports "volts" */
            setControl(rotationCharacterization.withRotationalRate(output.in(Volts)));
            /* also log the requested output for SysId */
            SignalLogger.writeDouble("Rotational_Rate", output.in(Volts));
          },
          null,
          this));

  private Optional<AimParams> override = Optional.empty();

  /* The SysId routine to test */
  private SysIdRoutine sysIdRoutineToApply = sysIdRoutineTranslation;

  /**
   * Constructs a CTRE SwerveDrivetrain using the specified constants.
   * <p>
   * This constructs the underlying hardware devices, so users should not construct the devices
   * themselves. If they need the devices, they can access them through getters in the classes.
   *
   * @param drivetrainConstants Drivetrain-wide constants for the swerve drive
   * @param modules Constants for each specific module
   */
  public Drivetrain(
      SwerveDrivetrainConstants drivetrainConstants,
      SwerveModuleConstants<?, ?, ?>... modules) {
    super(drivetrainConstants, modules);
    if (Utils.isSimulation()) {
      startSimThread();
    }
    state = getState();
    OnboardLogger ologger = new OnboardLogger("Drivetrain");
    ologger.registerBoolean("Valid Odometry", validOdemetry());
    ologger.registerDouble("Time since last estimate",
        () -> Timer.getTimestamp() - lastOkayVisionUpdateTime);
    SwerveModule<TalonFX, TalonFX, CANcoder>[] actualModules = getModules();
    for (int i = 0; i < actualModules.length; i++) {
      TalonFX drive, steer;
      SwerveModule<TalonFX, TalonFX, CANcoder> module = actualModules[i];
      drive = module.getDriveMotor();
      steer = module.getSteerMotor();

      StatusSignalUtil.registerCANivoreSignals(
          drive.getSupplyCurrent(false),
          drive.getStatorCurrent(false),
          drive.getMotorVoltage(false),
          drive.getDeviceTemp(false),
          drive.getVelocity(false),

          steer.getSupplyCurrent(false),
          steer.getStatorCurrent(false),
          steer.getMotorVoltage(false),
          steer.getDeviceTemp(false),
          steer.getVelocity(false),
          steer.getPosition(false));

      ologger.registerBoolean("Drive " + drive.getDeviceID() + " Connected",
          () -> BaseStatusSignal.isAllGood(
              drive.getSupplyCurrent(false),
              drive.getStatorCurrent(false),
              drive.getMotorVoltage(false),
              drive.getDeviceTemp(false),
              drive.getVelocity(false)));
      ologger.registerMeasurement("Drive " + drive.getDeviceID() + " Supply Current",
          drive.getSupplyCurrent(false)::getValue, Amps);
      ologger.registerMeasurement("Drive " + drive.getDeviceID() + " Stator Current",
          drive.getStatorCurrent(false)::getValue, Amps);
      ologger.registerMeasurement("Drive " + drive.getDeviceID() + " Voltage",
          drive.getMotorVoltage(false)::getValue, Volts);
      ologger.registerMeasurement("Drive " + drive.getDeviceID() + " Temperature",
          drive.getDeviceTemp(false)::getValue, Celsius);
      ologger.registerMeasurement("Drive " + drive.getDeviceID() + " Velocity",
          drive.getVelocity(false)::getValue, RotationsPerSecond);

      ologger.registerBoolean("Steer " + steer.getDeviceID() + " Connected",
          () -> BaseStatusSignal.isAllGood(
              steer.getSupplyCurrent(false),
              steer.getStatorCurrent(false),
              steer.getMotorVoltage(false),
              steer.getDeviceTemp(false),
              steer.getVelocity(false),
              steer.getPosition(false)));
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Supply Current",
          steer.getSupplyCurrent(false)::getValue, Amps);
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Stator Current",
          steer.getStatorCurrent(false)::getValue, Amps);
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Voltage",
          steer.getMotorVoltage(false)::getValue, Volts);
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Temperature",
          steer.getDeviceTemp(false)::getValue, Celsius);
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Velocity",
          steer.getVelocity(false)::getValue, RotationsPerSecond);
      ologger.registerMeasurement("Steer " + steer.getDeviceID() + " Position",
          steer.getPosition(false)::getValue, Rotations);
    }
    sysIDCommands();
    // SmartDashboard.putData("Drivetrain/Set Home", setMemorySpot());
    // SmartDashboard.putData("Drivetrain/Go Home", goHome());
    SmartDashboard.putData("Drivetrain/Shake", shake());
    configurePathplanner();
  }

  /**
   * Returns a command that applies the specified control request to this swerve drivetrain.
   *
   * @param request Function returning the request to apply
   * @return Command to run
   */
  private Command applyRequest(Supplier<SwerveRequest> request) {
    return run(() -> this.setControl(request.get()));
  }

  /**
   * Runs the SysId Quasistatic test in the given direction for the routine specified by
   * {@link #sysIdRoutineToApply}.
   *
   * @param direction Direction of the SysId Quasistatic test
   * @return Command to run
   */
  public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
    return sysIdRoutineToApply.quasistatic(direction);
  }

  /**
   * Runs the SysId Dynamic test in the given direction for the routine specified by
   * {@link #sysIdRoutineToApply}.
   *
   * @param direction Direction of the SysId Dynamic test
   * @return Command to run
   */
  public Command sysIdDynamic(SysIdRoutine.Direction direction) {
    return sysIdRoutineToApply.dynamic(direction);
  }

  public Command sysIdQuasistaticSteer(SysIdRoutine.Direction direction) {
    return sysIdRoutineSteer.quasistatic(direction);
  }

  public Command sysIdDynamicSteer(SysIdRoutine.Direction direction) {
    return sysIdRoutineSteer.dynamic(direction);
  }

  @Override
  public void periodic() {
    /*
     * Periodically try to apply the operator perspective. If we haven't applied the operator
     * perspective before, then we should apply it regardless of DS state. This allows us to correct
     * the perspective in case the robot code restarts mid-match. Otherwise, only check and apply
     * the operator perspective if the DS is disabled. This ensures driving behavior doesn't change
     * until an explicit disable event occurs during testing.
     */
    if (!hasAppliedOperatorPerspective || DriverStation.isDisabled()) {
      DriverStation.getAlliance().ifPresent(allianceColor -> {
        setOperatorPerspectiveForward(
            allianceColor == Alliance.Red
                ? kRedAlliancePerspectiveRotation
                : kBlueAlliancePerspectiveRotation);
        hasAppliedOperatorPerspective = true;
      });
    }

    state = getState();

    FieldManager.getInstance().getField().setRobotPose(robotPose());
  }

  private void startSimThread() {
    lastSimTime = Utils.getCurrentTimeSeconds();
    /* Run simulation at a faster rate so PID gains behave more reasonably */
    simNotifier = new Notifier(() -> {
      final double currentTime = Utils.getCurrentTimeSeconds();
      double deltaTime = currentTime - lastSimTime;
      lastSimTime = currentTime;

      /* use the measured time delta, get battery voltage from WPILib */
      updateSimState(deltaTime, RobotController.getBatteryVoltage());
    });
    simNotifier.startPeriodic(kSimLoopPeriod);
  }

  /**
   * Adds a vision measurement to the Kalman Filter. This will correct the odometry pose estimate
   * while still accounting for measurement noise.
   *
   * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
   * @param timestampSeconds The timestamp of the vision measurement in seconds.
   */
  @Override
  public void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds) {
    super.addVisionMeasurement(visionRobotPoseMeters, Utils.fpgaToCurrentTime(timestampSeconds));
  }

  /**
   * Adds a vision measurement to the Kalman Filter. This will correct the odometry pose estimate
   * while still accounting for measurement noise.
   * <p>
   * Note that the vision measurement standard deviations passed into this method will continue to
   * apply to future measurements until a subsequent call to
   * {@link #setVisionMeasurementStdDevs(Matrix)} or this method.
   *
   * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
   * @param timestampSeconds The timestamp of the vision measurement in seconds.
   * @param visionMeasurementStdDevs Standard deviations of the vision pose measurement in the form
   *        [x, y, theta]^T, with units in meters and radians.
   */
  @Override
  public void addVisionMeasurement(
      Pose2d visionRobotPoseMeters,
      double timestampSeconds,
      Matrix<N3, N1> visionMeasurementStdDevs) {
    super.addVisionMeasurement(visionRobotPoseMeters, Utils.fpgaToCurrentTime(timestampSeconds),
        visionMeasurementStdDevs);
  }

  /**
   * Return the pose at a given timestamp, if the buffer is not empty.
   *
   * @param timestampSeconds The timestamp of the pose in seconds.
   * @return The pose at the given timestamp (or Optional.empty() if the buffer is empty).
   */
  @Override
  public Optional<Pose2d> samplePoseAt(double timestampSeconds) {
    return super.samplePoseAt(Utils.fpgaToCurrentTime(timestampSeconds));
  }

  public Command teleopDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot,
      Supplier<TeleopDriveMode> modeSupplier) {
    return this.applyRequest(() -> {
      TeleopDriveMode mode = modeSupplier.get();
      // Recalculate the *real* vx and vy to be operator-dependent
      Translation2d operatorRelative =
          new Translation2d(vx.getAsDouble() * maxSpeed, vy.getAsDouble() * maxSpeed);

      Translation2d fieldRelative = operatorRelative.rotateBy(getOperatorForwardDirection());
      double spin = vrot.getAsDouble() * maxRotationalSpeed;

      if (mode == TeleopDriveMode.RobotRelative) {
        return robotCentricDrive
            .withVelocityX(operatorRelative.getX())
            .withVelocityY(operatorRelative.getY())
            .withRotationalRate(spin);
      }

      if (mode == TeleopDriveMode.SlowFieldRelativeSpin) {
        fieldRelative = fieldRelative.times(0.3);
        spin *= 0.5;
      }

      if (override.isPresent()) {
        return drivetrainAim.withVelocityX(fieldRelative.getX())
            .withVelocityY(fieldRelative.getY())
            .withTargetDirection(override.get().yaw);
      }

      return drive
          .withVelocityX(fieldRelative.getX())
          .withVelocityY(fieldRelative.getY())
          .withRotationalRate(spin);
    })
        .withName("Teleop Drive");
  }

  public Command teleopDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot) {
    return teleopDrive(vx, vy, vrot, () -> TeleopDriveMode.FieldRelativeSpin);
  }

  /**
   * Returns the drivetrain's estimated pose on the field.
   */
  public Pose2d robotPose() {
    return state.Pose;
  }

  /**
   * Returns the drivetrain's field-relative velocity.
   */
  public Transform2d robotVelocity() {
    ChassisSpeeds fieldRelative =
        ChassisSpeeds.fromRobotRelativeSpeeds(state.Speeds, robotPose().getRotation());
    return new Transform2d(
        fieldRelative.vxMetersPerSecond,
        fieldRelative.vyMetersPerSecond,
        Rotation2d.fromRadians(fieldRelative.omegaRadiansPerSecond));
  }

  public Trigger validOdemetry() {
    return new Trigger(
        () -> Timer.getTimestamp()
            - lastOkayVisionUpdateTime <= LocalizationConstants.kValidOdometryCutoff);
  }

  public void addPoseEstimate(TimestampedPoseEstimate estimate) {
    lastOkayVisionUpdateTime = Timer.getTimestamp();
    // This should NOT run in simulation!
    if (Robot.isSimulation()) {
      return;
    }
    // SmartDashboard.putNumber("stddevs", estimate.stdDevs().get(0,0));
    addVisionMeasurement(
        estimate.pose(),
        estimate.timestamp(),
        estimate.stdDevs());
  }

  public Command rotate() {
    return this.applyRequest(
        () -> new SwerveRequest.RobotCentric().withRotationalRate(0.5 * Math.PI));
  }

  private void sysIDCommands() {
    // SmartDashboard.putData("sysID/dynamic forward steer",
    // sysIdDynamicSteer(Direction.kForward));
    // SmartDashboard.putData("sysID/dynamic reverse steer",
    // sysIdDynamicSteer(Direction.kReverse));
    // SmartDashboard.putData("sysID/dynamic forward drive",
    // sysIdDynamic(Direction.kForward));
    // SmartDashboard.putData("sysID/dynamic reverse drive",
    // sysIdDynamic(Direction.kReverse));
    // SmartDashboard.putData("sysID/quasistatic forward drive",
    // sysIdQuasistatic(Direction.kForward));
    // SmartDashboard.putData("sysID/quasistatic reverse drive",
    // sysIdQuasistatic(Direction.kReverse));
    // SmartDashboard.putData("sysID/quasistatic reverse steer",
    // sysIdQuasistaticSteer(Direction.kReverse));
    // SmartDashboard.putData("sysID/ quasistatic forward steer",
    // sysIdQuasistaticSteer(Direction.kForward));
  }

  private void stop() {
    setControl(new SwerveRequest.SwerveDriveBrake());
  }

  public Command driveTo(Supplier<APTarget> target, Autopilot autopilot) {
    return this.run(() -> {
      APResult result = autopilot.calculate(robotPose(), state.Speeds, target.get());
      FieldManager.getInstance().getField().getObject("Target")
          .setPose(target.get().getReference());
      setControl(autopilotControl
          .withVelocityX(result.vx())
          .withVelocityY(result.vy())
          .withTargetDirection(override.isPresent() ? override.get().yaw : result.targetAngle()));
    })
        .until(() -> autopilot.atTarget(robotPose(), target.get()))
        .finallyDo(() -> {
          // Only stop if we are supposed to.
          if (target.get().getVelocity() == 0) {
            stop();
          }
        });
  }

  public Command resetOdometry(Pose2d pose, boolean flip) {
    return this.runOnce(() -> {
      resetPose(flip ? FieldUtils.allianceRelativeFlip(pose) : pose);
    }).ignoringDisable(true);
  }

  private Twist2d predictedTwist() {
    return new Twist2d(
        state.Speeds.vxMetersPerSecond * LOOKAHEAD,
        state.Speeds.vyMetersPerSecond * LOOKAHEAD,
        state.Speeds.omegaRadiansPerSecond * LOOKAHEAD);
  }

  public Pose2d predictedRobotPose() {
    return robotPose().exp(predictedTwist());
  }

  public Translation2d predictedRobotVelocity() {
    return robotVelocity().getTranslation().rotateBy(
        Rotation2d.fromRadians(state.Speeds.omegaRadiansPerSecond * LOOKAHEAD));
  }

  public Command track(Supplier<AimParams> params) {
    return Commands.run(() -> override = Optional.of(params.get()))
        .finallyDo(() -> override = Optional.empty());
  }

  public Command setMemorySpot() {
    return Commands.runOnce(() -> memorySpot = robotPose());
  }

  public Command goHome() {
    return driveTo(() -> new APTarget(memorySpot), AutopilotConstants.kDefaultAutopilot);
  }

  private void driveRobotRelative(ChassisSpeeds speeds) {
    setControl(robotSpeeds.withSpeeds(speeds));
  }

  private Command shakeAbout(Supplier<Pose2d> center) {
    APTarget[] target = new APTarget[] {null};
    return Commands.sequence(
        this.runOnce(() -> {
          Rotation2d randomAngle = Rotation2d.fromRotations(Math.random());
          double radius = 0.5;
          target[0] = new APTarget(center.get().plus(new Transform2d(
              radius * randomAngle.getCos(),
              radius * randomAngle.getSin(),
              Rotation2d.kZero)));
        }),
        driveTo(() -> target[0], AutopilotConstants.kLooseAutopilot))

        .repeatedly();
  }

  public Command shake() {
    Pose2d[] start = new Pose2d[] {null};
    return Commands.sequence(
        this.runOnce(() -> start[0] = robotPose()),
        this.shakeAbout(() -> start[0]))

        .withName("Drivetrain Shake");
  }

  private void configurePathplanner() {
    RobotConfig config;
    try {
      config = RobotConfig.fromGUISettings();
    } catch (Exception e) {
      // Handle exception as needed
      e.printStackTrace();
      return;
    }

    // Configure AutoBuilder last
    AutoBuilder.configure(
        this::robotPose, // Robot pose supplier
        this::resetPose, // Method to reset odometry (will be called if your auto has a starting
                         // pose)
        () -> state.Speeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
        (speeds, feedforwards) -> driveRobotRelative(speeds), // Method that will drive the robot
                                                              // given ROBOT RELATIVE
                                                              // ChassisSpeeds. Also optionally
                                                              // outputs individual
                                                              // module feedforwards
        new PPHolonomicDriveController( // PPHolonomicController is the built in path following
                                        // controller for holonomic
                                        // drive trains
            new PIDConstants(1.7, 0.0, 0.0), // Translation PID constants
            new PIDConstants(3.0, 0.0, 0.0) // Rotation PID constants
        ),
        config, // The robot configuration
        () -> {
          // Boolean supplier that controls when the path will be mirrored for the red
          // alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
            return alliance.get() == DriverStation.Alliance.Red;
          }
          return false;
        },
        this // Reference to this subsystem to set requirements
    );
  }
}
