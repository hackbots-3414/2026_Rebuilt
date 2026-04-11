package frc.robot.superstructure;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.CommandBuilder;
import frc.robot.generated.CompBotTunerConstants;
import frc.robot.generated.TestBotTunerConstants;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.climber.ClimberIOSim;
import frc.robot.subsystems.drivetrain.Drivetrain;
import frc.robot.subsystems.drivetrain.Drivetrain.TeleopDriveMode;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.IndexerIOHardware;
import frc.robot.subsystems.indexer.IndexerIOSim;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeIOHardware;
import frc.robot.subsystems.intake.IntakeIOSim;
import frc.robot.subsystems.led.Led;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterIOHardware;
import frc.robot.subsystems.shooter.ShooterIOSim;
import frc.robot.subsystems.turret.Turret;
import frc.robot.subsystems.turret.TurretIOHardware;
import frc.robot.subsystems.turret.TurretIOSim;
import frc.robot.superstructure.StateManager.ShootMode;
import frc.robot.util.RobotIdentifier;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.localization.AprilTagVisionHandler;
import frc.robot.vision.localization.LocalizationConstants;
import frc.robot.vision.localization.TimestampedPoseEstimate;

/**
 * This class is important
 */
public class Superstructure {
  public record Subsystems(
      Drivetrain drivetrain,
      Turret turret,
      Shooter shooter,
      Indexer indexer,
      Intake intake,
      Climber climber,
      Led led) {
  }

  private final Subsystems subsystems;
  public final StateManager state;

  public Superstructure() {
    subsystems = switch (RobotIdentifier.id()) {
      case CompBot -> createCompBotSubsystems();
      case SimBot -> createSimBotSubsystems();
      case TestBot -> createTestBotSubsystems();
    };
    state = new StateManager(subsystems);
  }

  /** Creates subsystems initialized for real comp robot. */
  private Subsystems createCompBotSubsystems() {
    Drivetrain drivetrain = CompBotTunerConstants.createDrivetrain();
    Turret turret = new Turret(new TurretIOHardware());
    Shooter shooter = new Shooter(new ShooterIOHardware());
    Indexer indexer = new Indexer(new IndexerIOHardware());
    Intake intake = new Intake(new IntakeIOHardware());
    Climber climber = new Climber(new ClimberIOSim());
    Led led = new Led(new LedIO());
    return new Subsystems(drivetrain, turret, shooter, indexer, intake, climber, led);
  }

  /** Creates subsystems initialized for simulation robot */
  private Subsystems createSimBotSubsystems() {
    Drivetrain drivetrain = CompBotTunerConstants.createDrivetrain();
    Turret turret = new Turret(new TurretIOSim());
    Shooter shooter = new Shooter(new ShooterIOSim());
    Indexer indexer = new Indexer(new IndexerIOSim());
    Intake intake = new Intake(new IntakeIOSim());
    Climber climber = new Climber(new ClimberIOSim());
    Led led = new Led(new LedIO());
    return new Subsystems(drivetrain, turret, shooter, indexer, intake, climber, led);
  }

  /** Creates subsystems initialized for test bot */
  private Subsystems createTestBotSubsystems() {
    Drivetrain drivetrain = TestBotTunerConstants.createDrivetrain();
    // Sim everything besides drivetrain and LEDs.
    Turret turret = new Turret(new TurretIOSim());
    Shooter shooter = new Shooter(new ShooterIOSim());
    Indexer indexer = new Indexer(new IndexerIOSim());
    Intake intake = new Intake(new IntakeIOSim());
    Climber climber = new Climber(new ClimberIOSim());
    Led led = new Led(new LedIO());
    return new Subsystems(drivetrain, turret, shooter, indexer, intake, climber, led);
  }

  public void bindDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot, BooleanSupplier slowbot) {
    subsystems.drivetrain.setDefaultCommand(subsystems.drivetrain.teleopDrive(vx, vy, vrot, () -> {
      if (slowbot.getAsBoolean()) {
        return TeleopDriveMode.AccelerationLimitedFieldRelative;
      }
      if (state.shooting(ShootMode.Scoring).getAsBoolean()) {
        return TeleopDriveMode.SlowFieldRelative;
      }
      return TeleopDriveMode.FieldRelative;
    }));
  }

  /**
   * Builds a command from a command builder, and returns it. This also sets that command to run as
   * a proxied command, because it's helpful for autons.
   */
  public Command build(CommandBuilder builder) {
    return buildWithoutProxy(builder).asProxy();
  }

  /**
   * The same thing as <code>build()</code>, except this is NOT a proxied command. This should be
   * used for default commands, where the command needs to explicity list its subsystems. However,
   * other than that, there aren't many uses for this method, so <b>use with care!</b>.
   */
  public Command buildWithoutProxy(CommandBuilder builder) {
    return builder.build(subsystems, state)
        .withName(builder.getClass().getSimpleName());
  }

  public AprilTagVisionHandler createAprilTagVisionHandler() {
    // These configs could be somewhere else.
    List<CameraConfig> configs = List.of(
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam1",
            () -> new Transform3d(-0.207, -0.318, 0.473,
                new Rotation3d(Units.degreesToRadians(0.7), Units.degreesToRadians(-28.578),
                    Units.degreesToRadians(-67.63)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam2",
            () -> new Transform3d(0.221, -0.262, 0.724,
                new Rotation3d(0, Units.degreesToRadians(-30), Units.degreesToRadians(26.3)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam3",
            () -> new Transform3d(0.121, 0.271, 0.709,
                new Rotation3d(0, Units.degreesToRadians(-5.1), Units.degreesToRadians(141.7)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam4",
            () -> new Transform3d(-0.315, 0.138, 0.438,
                new Rotation3d(Units.degreesToRadians(-6.5), Units.degreesToRadians(-29.9),
                    Units.degreesToRadians(-169)))));

    return new AprilTagVisionHandler(this, configs);
  }

  public void addPoseEstimate(TimestampedPoseEstimate estimate) {
    subsystems.drivetrain().addPoseEstimate(estimate);
  }

  public void periodic() {
    state.update();
    subsystems.led().update(state);
  }
}
