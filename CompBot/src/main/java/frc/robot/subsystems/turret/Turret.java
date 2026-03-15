package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Rotations;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.turret.TurretIO.TurretIOInputs;
import frc.robot.superstructure.StateManager;
import frc.robot.util.OnboardLogger;
import frc.robot.vision.localization.LocalizationConstants;

public class Turret extends SubsystemBase {

  private final TurretIO io;
  private final TurretIOInputs inputs;

  private final Alert calibrationAlert =
      new Alert("Turret not calibrated successfully", AlertType.kError);

  private boolean tracking;

  private Angle reference = TurretConstants.kHomePosition;

  public Turret(TurretIO io) {
    super();
    this.io = io;
    inputs = new TurretIOInputs();
    io.calibrate();
    SmartDashboard.putData("Turret/Home", home());
    Command calibrate = runOnce(io::calibrate).ignoringDisable(true);
    SmartDashboard.putData("Turret/Calibrate", calibrate);
    RobotModeTriggers.disabled().onTrue(calibrate);

    OnboardLogger log = new OnboardLogger("Turret");
    log.registerBoolean("Ready", ready());
    log.registerBoolean("Tracking", () -> tracking);
    log.registerMeasurement("Reference", () -> reference, Rotations);
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
  public Command track(StateManager state) {
    return Commands.sequence(
        this.run(() -> {
          tracking = true;
          Rotation2d robot = state.robotPose().getRotation();
          AimParams params = state.predictedAimParams();
          if (!params.isOk()) {
            return;
          }
          Angle mechanismAngle = params.yaw.minus(robot).getMeasure().plus(TurretConstants.kForwards);
          // We're only in "tracking" mode if we're just trying to get to a happy spot. If everybody
          // else is ready, we don't want to hold up shooting, so we allow the turret access to its
          // full range. We don't generally want to do this, because it would mean that while
          // shooting, we would be more likely to hit the turret's physical max and *force*
          // ourselves to rotate the turret all the way around... nonideal.
          setPosition(mechanismAngle, !state.shootReady.getAsBoolean());
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
  public Trigger ready() {
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

  public Pose3d turretPose(Pose2d robotPose) {
    return new Pose3d(robotPose).transformBy(TurretConstants.kOffset);
}

  /**
   * This algorithm calculates the "ideal" position for the turret to rotate through.
   */
  private void setPosition(Angle position, boolean tracking) {
    Angle min = TurretConstants.kMinAngle;
    Angle max = TurretConstants.kMaxAngle;
    reference = Rotations.of(findCC(
        inputs.position.in(Rotations),
        position.in(Rotations),
        min.in(Rotations),
        max.in(Rotations)));
    io.setPosition(reference);
  }

  /**
   * Returns the Closest Conguent value in the range [min,max], modulo 1
   *
   * @param position the current, non-wrapped position
   * @param reference the goal position, in the range [0,1]
   * @param min the minimum position
   * @param max the maximum position
   */
  protected static double findCC(double position, double reference, double min, double max) {
    // Ensure reference is already a valid reference
    while (reference > max) {
      reference -= 1.0;
    }

    while (reference < min) {
      reference += 1.0;
    }

    if (reference > max) {
      // We've reached a reference that CANNOT be valid, ever. Bummer, just stay put i guess?
      return max;
    }

    double error = Math.abs(reference - position);
    if (error < 0.5) {
      return reference;
    }
    double offset = (reference > position) ? -1 : 1;
    while (true) {
      double newReference = reference + offset;
      if (newReference > max || newReference < min) {
        break;
      }
      double newError = Math.abs(newReference - position);
      if (Math.abs(newError) < 0.5) {
        // This is the closest we'll get
        return newReference;
      }
      reference = newReference;
    }
    return reference;
  }

  public Transform3d turretCameraOffset() {
    // Get turret-relative position
    Transform3d turretRelative =
        new Transform3d(Translation3d.kZero, new Rotation3d(new Rotation2d(inputs.position.minus(TurretConstants.kForwards))))
            .plus(LocalizationConstants.kTurretAoRToTurretCameraOffset);
    return TurretConstants.kOffset.plus(turretRelative);
  }
}
