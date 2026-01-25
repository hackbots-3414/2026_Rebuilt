package frc.robot.subsystems.Turret;

import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.FieldManager;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.Turret.TurretIO.TurretIOInputs;
import frc.robot.superstructure.StateManager;
import frc.robot.util.OnboardLogger;

public class Turret extends SubsystemBase {

  private final TurretIO io;
  private final TurretIOInputs inputs;

  private final Alert calibrationAlert =
      new Alert("Turret not calibrated successfully", AlertType.kError);

  private boolean tracking;

  public Turret(TurretIO io) {
    super();
    this.io = io;
    inputs = new TurretIOInputs();
    io.calibrate();
    SmartDashboard.putData("Turret/Home", home());
    SmartDashboard.putData("Turret/Calibrate", runOnce(io::calibrate));

    OnboardLogger log = new OnboardLogger("Turret");
    log.registerBoolean("Ready", ready());
    log.registerBoolean("Tracking", () -> tracking);
    log.registerBoolean("Tracked", tracked());
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    calibrationAlert.set(!inputs.calibrated);
  }

  /**
   * Returns a {@link Command} that will continually attempt to listen to the {@link AimParams}
   * object that is supplied, accounting for the robot's rotation.
   */
  public Command track(StateManager state, Supplier<AimParams> aimParams) {
    return Commands.sequence(
        runOnce(() -> tracking = true),
        this.run(() -> {
          Rotation2d robot = state.robotPose().getRotation();
          Rotation2d relative = aimParams.get().yaw.minus(robot);
          io.setPosition(relative.getMeasure().plus(TurretConstants.kTrackingOffset));
        }))
    .finallyDo(() -> tracking = false);
  }

  /**
   * Returns a command that sends the turret to its home position.
   */
  public Command home() {
    return Commands.sequence(
        runOnce(() -> io.setPosition(TurretConstants.kHomePosition)),
        Commands.waitUntil(ready()));
  }

  /**
   * Returns a {@link Trigger} that represents whether the turret is currently at its reference
   * position.
   */
  public Trigger ready() {
    return new Trigger(() -> {
      double delta = inputs.position.minus(inputs.reference).baseUnitMagnitude();
      return Math.abs(delta) <= TurretConstants.kTolerance.baseUnitMagnitude();
    });
  }

  public Trigger tracked() {
    return ready().and(new Trigger(() -> tracking));
  }

  public void telemetrize(StateManager state) {
    Pose2d turretPosition = state.robotPose().transformBy(TurretConstants.kTurretPosition.plus(
        new Transform2d(Translation2d.kZero,
            new Rotation2d(inputs.position.minus(TurretConstants.kTrackingOffset)))));
    FieldManager.getInstance().getField().getObject("turret").setPose(turretPosition);
  }
}
