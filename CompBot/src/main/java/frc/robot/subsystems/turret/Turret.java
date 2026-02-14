package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Revolutions;
import static edu.wpi.first.units.Units.Rotations;
import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.FieldManager;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.turret.TurretIO.TurretIOInputs;
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
    SmartDashboard.putData("Turret/Calibrate", runOnce(io::calibrate).ignoringDisable(true));

    OnboardLogger log = new OnboardLogger("Turret");
    log.registerBoolean("Ready", ready());
    log.registerBoolean("Tracking", () -> tracking);
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    SmartDashboard.putNumber("Position", inputs.position.in(Revolutions));
    calibrationAlert.set(!inputs.calibrated);
  }

  /**
   * Returns a {@link Command} that will continually attempt to listen to the {@link AimParams}
   * object that is supplied, accounting for the robot's rotation.
   */
  public Command track(StateManager state) {
    return Commands.sequence(
        this.run(() -> {
          tracking = true;
          Rotation2d robot = state.robotPose().getRotation();
          Rotation2d relative = state.predictedAimParams().yaw.minus(robot);
          // We're only in "tracking" mode if we're just trying to get to a happy spot. If everybody
          // else is ready, we don't want to hold up shooting, so we allow the turret access to its
          // full range. We don't generally want to do this, because it would mean that while
          // shooting, we would be more likely to hit the turret's physical max and *force*
          // ourselves to rotate the turret all the way around... nonideal.
          setPosition(relative.getMeasure().plus(TurretConstants.kForwards),
              !state.shootReady().getAsBoolean());
        }))
        .finallyDo(() -> tracking = false);
  }

  /**
   * Returns a command that sends the turret to its home position.
   */
  public Command home() {
    return Commands.sequence(
        runOnce(() -> setPosition(TurretConstants.kHomePosition, false)),
        Commands.waitUntil(ready()));
  }

  public Command forwards() {
    return Commands.sequence(
        runOnce(() -> setPosition(TurretConstants.kForwards, false)),
        Commands.waitUntil(ready()));
  }

  /**
   * Returns a {@link Trigger} that represents whether the turret is currently at its reference
   * position.
   */
  private Trigger ready() {
    return new Trigger(() -> {
      double delta = inputs.position.minus(inputs.reference).baseUnitMagnitude();
      return Math.abs(delta) <= TurretConstants.kTolerance.baseUnitMagnitude();
    });
  }

  public Trigger tracked(Supplier<AimParams> params) {
    return new Trigger(() -> {
      double delta = inputs.position.minus(inputs.reference).baseUnitMagnitude();
      double epsilon = params.get().deltaYaw.getMeasure().baseUnitMagnitude();
      return Math.abs(delta) <= epsilon && tracking;
    });
  }

  public void telemetrize(StateManager state) {
    Pose2d turretPosition = state.robotPose().transformBy(TurretConstants.kTurretPosition.plus(
        new Transform2d(Translation2d.kZero,
            new Rotation2d(inputs.position.minus(TurretConstants.kForwards)))));
    Pose2d turretReference = state.robotPose().transformBy(TurretConstants.kTurretPosition.plus(
        new Transform2d(Translation2d.kZero,
            new Rotation2d(inputs.reference.minus(TurretConstants.kForwards)))));
    FieldManager.getInstance().getField().getObject("turret").setPose(turretPosition);
    FieldManager.getInstance().getField().getObject("turret-target").setPose(turretReference);
  }

  public Pose3d turretPose(Pose2d robotPose) {
    return new Pose3d(robotPose).transformBy(TurretConstants.kOffset);
  }

  /**
   * This algorithm calculates the "ideal" position for the turret to rotate through.
   */
  private void setPosition(Angle position, boolean tracking) {
    Angle min = (tracking) ? TurretConstants.kMinTrackingAngle : TurretConstants.kMinAngle;
    Angle max = (tracking) ? TurretConstants.kMaxTrackingAngle : TurretConstants.kMaxAngle;
    io.setPosition(Rotations.of(findCC(
        inputs.position.in(Rotations),
        position.in(Rotations),
        min.in(Rotations),
        max.in(Rotations))));
  }

  /**
   * Returns the Closest Conguent value in the range [min,max], modulo 1
   *
   * WARNING: this only works if you're SURE that max - min >= 1
   *
   * @param position the current, non-wrapped position
   * @param reference the goal position, in the range [0,1]
   * @param min the minimum position
   * @param max the maximum position
   */
  protected static double findCC(double position, double reference, double min, double max) {
    if (max - min < 1.0) {
      DriverStation.reportError(
          "Invalid range passed to Turret.findCC: " + min + " to " + max,
          true);
      return position;
    }
    // Ensure reference is already a valid reference
    while (reference > max) {
      reference -= 1.0;
    }
    while (reference < min) {
      reference += 1.0;
    }
    double error = Math.abs(reference - position);
    if (Math.abs(error) < 0.5) {
      return reference;
    }
    double offset = (reference > position) ? -1 : 1;
    while (true) {
      double newReference = reference + offset;
      if (newReference > max || newReference < min) {
        break;
      }
      double newError = Math.abs(newReference - position);
      if (newError > error) {
        break;
      }
      if (Math.abs(newError) < 0.5) {
        // This is the closest we'll get
        return newReference;
      }
      reference = newReference;
    }
    return reference;
  }
}
