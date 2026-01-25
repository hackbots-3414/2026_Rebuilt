package frc.robot.superstructure;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.commands.CommandBuilder;
import frc.robot.generated.TestBotTunerConstants;
import frc.robot.subsystems.Turret.Turret;
import frc.robot.subsystems.Turret.TurretIOHardware;
import frc.robot.subsystems.Turret.TurretIOSim;
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
import frc.robot.vision.localization.AprilTagVisionHandler;
import frc.robot.vision.localization.TimestampedPoseEstimate;

public class Superstructure {
  public record Subsystems(
      Drivetrain drivetrain,
      Turret turret,
      Shooter shooter,
      Indexer indexer,
      Intake intake) {
  }

  private final Subsystems subsystems;
  public final StateManager state;

  public Superstructure() {
    Drivetrain drivetrain = TestBotTunerConstants.createDrivetrain();
    Turret turret = new Turret(Robot.isReal() ? new TurretIOHardware() : new TurretIOSim());
    Shooter shooter = new Shooter(Robot.isReal() ? new ShooterIOHardware() : new ShooterIOSim());
    Indexer indexer = new Indexer(Robot.isReal() ? new IndexerIOHardware() : new IndexerIOSim());
    Intake intake = new Intake(Robot.isReal() ? new IntakeIOHardware() : new IntakeIOSim());
    subsystems = new Subsystems(drivetrain, turret, shooter, indexer, intake);
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
    return new AprilTagVisionHandler(this);
  }

  public void addPoseEstimate(TimestampedPoseEstimate estimate) {
    subsystems.drivetrain().addPoseEstimate(estimate);
  }

  public void periodic() {
    state.periodic();
    subsystems.turret.telemetrize(state);
  }
}
