package frc.robot.superstructure;

import static edu.wpi.first.units.Units.Degrees;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Robot;
import frc.robot.Constants.AimConstants;
import frc.robot.aiming.AimConstraints;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.util.FieldUtils;
import frc.robot.util.OnboardLogger;

/**
 * A class representing the robot-wide state variables.
 */
public class StateManager {
  private final Subsystems subsystems;

  private AimParams params = new AimParams(AimStatus.Unchecked);
  private AimParams predictedParams = new AimParams(AimStatus.Unchecked);

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
    log.registerBoolean("In Alliance Zone", () -> FieldUtils.inAllianceZone(robotPose()));

    String aimPrefix = "Aim Params/";
    log.registerString(aimPrefix + "Status", () -> params.status.toString());
    log.registerMeasurement(aimPrefix + "Pitch", () -> params.pitch.getMeasure(), Degrees);
    log.registerMeasurement(aimPrefix + "Yaw", () -> params.yaw.getMeasure(), Degrees);
    log.registerDouble(aimPrefix + "Velocity", () -> params.output);
    log.registerMeasurement(aimPrefix + "Error/Pitch", () -> params.deltaPitch.getMeasure(),
        Degrees);
    log.registerMeasurement(aimPrefix + "Error/Yaw", () -> params.deltaYaw.getMeasure(), Degrees);
    log.registerDouble(aimPrefix + "Error/Velocity", () -> params.deltaOutput);

    aimPrefix = "Aim Params (Predicted)/";
    log.registerString(aimPrefix + "Status", () -> predictedParams.status.toString());
    log.registerMeasurement(aimPrefix + "Pitch", () -> predictedParams.pitch.getMeasure(), Degrees);
    log.registerMeasurement(aimPrefix + "Yaw", () -> predictedParams.yaw.getMeasure(), Degrees);
    log.registerDouble(aimPrefix + "Velocity", () -> predictedParams.output);
    log.registerMeasurement(aimPrefix + "Error/Pitch",
        () -> predictedParams.deltaPitch.getMeasure(), Degrees);
    log.registerMeasurement(aimPrefix + "Error/Yaw", () -> predictedParams.deltaYaw.getMeasure(),
        Degrees);
    log.registerDouble(aimPrefix + "Error/Velocity", () -> predictedParams.deltaOutput);
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
        .and(() -> params.isOk())
        .and(() -> DriverStation.isTeleop() || FieldUtils.inAllianceZone(robotPose()));
  }

  public AimParams aimParams() {
    if (params.status == AimStatus.Unchecked) {
      params =
          AimConstants.kAim.update(aimTarget(), turretPose(), robotVelocity().getTranslation());
    }
    return params;
  }

  public AimParams predictedAimParams() {
    if (predictedParams.status == AimStatus.Unchecked) {
      Pose2d predictedPose = subsystems.drivetrain().predictedRobotPose();
      predictedParams =
          AimConstants.kAim.update(aimTarget(), subsystems.turret().turretPose(predictedPose),
              subsystems.drivetrain().predictedRobotVelocity());
    }
    return predictedParams;
  }

  public void periodic() {
    params = new AimParams(AimStatus.Unchecked);
    predictedParams = new AimParams(AimStatus.Unchecked);
  }

  public Trigger climbing() {
    return subsystems.climber().wants(ClimbPosition.Climbed);
  }

  public Trigger climbed() {
    return subsystems.climber().at(ClimbPosition.Climbed);
  }

  public Pose3d turretPose() {
    return subsystems.turret().turretPose(robotPose());
  }

}
