package frc.robot.superstructure;

import java.util.List;
import java.util.function.DoubleSupplier;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.autogen.Autogen;
import frc.robot.Robot;
import frc.robot.commands.CommandBuilder;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.climber.ClimberIOHardware;
import frc.robot.subsystems.climber.ClimberIOSim;
import frc.robot.subsystems.drivetrain.Drivetrain;
import frc.robot.subsystems.indexer.Indexer;
import frc.robot.subsystems.indexer.IndexerIOHardware;
import frc.robot.subsystems.indexer.IndexerIOSim;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeIOHardware;
import frc.robot.subsystems.intake.IntakeIOSim;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterIOHardware;
import frc.robot.subsystems.shooter.ShooterIOSim;
import frc.robot.subsystems.turret.Turret;
import frc.robot.subsystems.turret.TurretIOHardware;
import frc.robot.subsystems.turret.TurretIOSim;
import frc.robot.util.AutonWarn;
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
      Climber climber) {
  }

  private final Subsystems subsystems;
  public final StateManager state;

  private SendableChooser<Command> autoChooser = new SendableChooser<>();
  private final Alert autonAlert =
      new Alert("Robot not in configured starting pose for auton", AlertType.kWarning);

  public Superstructure() {
    Drivetrain drivetrain = TunerConstants.createDrivetrain();
    Turret turret = new Turret(Robot.isReal() ? new TurretIOHardware() : new TurretIOSim());
    Shooter shooter = new Shooter(Robot.isReal() ? new ShooterIOHardware() : new ShooterIOSim());
    Indexer indexer = new Indexer(Robot.isReal() ? new IndexerIOHardware() : new IndexerIOSim());
    Intake intake = new Intake(Robot.isReal() ? new IntakeIOHardware() : new IntakeIOSim());
    Climber climber = new Climber(Robot.isReal() ? new ClimberIOHardware() : new ClimberIOSim());
    subsystems = new Subsystems(drivetrain, turret, shooter, indexer, intake, climber);
    state = new StateManager(subsystems);
  }

  public void bindDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot) {
    subsystems.drivetrain.setDefaultCommand(subsystems.drivetrain.teleopDrive(vx, vy, vrot));
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
        LocalizationConstants.kTurretBaseCameraConfig.cameraCopy(
            "turret",
            subsystems.turret()::turretCameraOffset),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam1",
            () -> new Transform3d(-0.203, -0.321, 0.514,
                new Rotation3d(0, Units.degreesToRadians(-28.6), Units.degreesToRadians(-53.654)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam2",
            () -> new Transform3d(0.228, -0.281, 0.719,
                new Rotation3d(0, Units.degreesToRadians(-30), Units.degreesToRadians(26.3)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam3",
            () -> new Transform3d(0.141, 0.307, 0.730,
                new Rotation3d(0, Units.degreesToRadians(-5.1), Units.degreesToRadians(141.3)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam4",
            () -> new Transform3d(-0.317, 0.138, 0.441,
                new Rotation3d(0, Units.degreesToRadians(-30), Units.degreesToRadians(-165.827)))));

    return new AprilTagVisionHandler(this, configs);
  }

  public void addPoseEstimate(TimestampedPoseEstimate estimate) {
    subsystems.drivetrain().addPoseEstimate(estimate);
  }

  public void periodic() {
    state.periodic();
    autonAlert.set(!AutonWarn.checkPose(getAutonomousCommand().getName(), state.robotPose()));
  }

  public void createAutonChooser() {
    autoChooser = Autogen.autoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
