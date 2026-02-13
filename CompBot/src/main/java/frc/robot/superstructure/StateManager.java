package frc.robot.superstructure;

import static edu.wpi.first.units.Units.Degrees;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.aiming.AimConstraints;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.util.FieldUtils;
import frc.robot.util.OnboardLogger;

/**
 * A class representing the robot-wide state variables.
 */
public class StateManager {
  private final Subsystems subsystems;
  private final AimStrategy aim = new PhysicsAim(
      new AimConstraints(Rotation2d.fromDegrees(49.5), Rotation2d.fromDegrees(72.0), 18),
      2,
      10);

  private AimParams params = AimParams.kImpossible;

  public StateManager(Subsystems subsystems) {
    this.subsystems = subsystems;
    OnboardLogger log = new OnboardLogger("Robot");
    log.registerPose("Robot Pose", this::robotPose);
    log.registerTransform2d("Robot Velocity", this::robotVelocity);
    log.registerPose3d("Aim Target", this::aimTarget);
    log.registerPose3d("Turret Position", this::turretPose);
    log.registerBoolean("Shoot Ready", shootReady());
    log.registerBoolean("Turret Tracked", subsystems.turret().tracked(this::aimParams));
    log.registerBoolean("Shooter Tracked", subsystems.shooter().tracked(this::aimParams));

    String aimPrefix = "Aim Params/";
    log.registerString(aimPrefix + "Status", () -> params.status.toString());
    log.registerMeasurement(aimPrefix + "Pitch", () -> params.pitch.getMeasure(), Degrees);
    log.registerMeasurement(aimPrefix + "Yaw", () -> params.yaw.getMeasure(), Degrees);
    log.registerDouble(aimPrefix + "Velocity", () -> params.velocity);
    log.registerMeasurement(aimPrefix + "Error/Pitch", () -> params.deltaPitch.getMeasure(),
        Degrees);
    log.registerMeasurement(aimPrefix + "Error/Yaw", () -> params.deltaYaw.getMeasure(), Degrees);
    log.registerDouble(aimPrefix + "Error/Velocity", () -> params.deltaVelocity);
  }

  /**
   * Returns the robot's position on the field.
   */
  public Pose2d robotPose() {
    return subsystems.drivetrain().robotPose();
  }

  /**
   * Returns the robot's field-relative velocity
   */
  public Transform2d robotVelocity() {
    return subsystems.drivetrain().robotVelocity();
  }

  public Pose3d aimTarget() {
    if (FieldUtils.inAllianceZone(robotPose())) {
      return FieldUtils.hub();
    } else {
      return FieldUtils.feedTarget();
    }
  }

  public Trigger shootReady() {
    return subsystems.turret().tracked(this::aimParams)
        .and(subsystems.shooter().tracked(this::aimParams))
        .and(() -> params.isOk());
  }

  public AimParams aimParams() {
    return params;
  }

  public void periodic() {
    params = aim.update(this);
  }

  public Pose3d turretPose() {
    return subsystems.turret().turretPose(this);
  }
}
