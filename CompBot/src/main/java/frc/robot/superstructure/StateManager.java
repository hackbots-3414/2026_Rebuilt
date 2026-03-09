package frc.robot.superstructure;

import static edu.wpi.first.units.Units.Degrees;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.AimConstants;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.util.ActivityCalculator;
import frc.robot.util.FieldUtils;
import frc.robot.util.OnboardLogger;

/**
 * A class representing the robot-wide state variables.
 */
public class StateManager {
  private final Subsystems subsystems;

  private AimParams params = new AimParams(AimStatus.Unchecked);
  private AimParams predictedParams = new AimParams(AimStatus.Unchecked);

  public final Trigger shootReady;
  public final Trigger forcedShootReady;

  public StateManager(Subsystems subsystems) {
    this.subsystems = subsystems;
    shootReady = shootReady();
    forcedShootReady = shootReady(true);
    OnboardLogger log = new OnboardLogger("Robot");
    log.registerPose("Robot Pose", this::robotPose);
    log.registerTransform2d("Robot Velocity", this::robotVelocity);
    log.registerPose3d("Aim Target", this::aimTarget);
    log.registerPose3d("Turret Position", this::turretPose);
    log.registerBoolean("Shoot Ready", shootReady);
    log.registerBoolean("Shoot Ready (Forced)", forcedShootReady);
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

  public Trigger shooting() {
    return subsystems.shooter().shooting();
  }

  public Trigger shouldShoot() {
    Trigger validdometry = subsystems.drivetrain().validOdemetry();
    return shooting().and(validdometry).and(() -> {
      boolean teleop = DriverStation.isTeleop();
      boolean inZone = FieldUtils.inAllianceZone(robotPose());
      boolean activeHubOrFeeding = !inZone || ActivityCalculator.is(ActivityCalculator.us());
      return (teleop || inZone) && activeHubOrFeeding;
    });
  }

  public Trigger shootReady(boolean forceWhenReady) {
    final double SHOOTER_DEBOUNCE = 1.5;
    final double TURRET_DEBOUNCE = 0.1;

    Trigger aimOk = new Trigger(() -> aimParams().isOk() && predictedAimParams().isOk());
    Trigger turretReady = subsystems.turret().tracked(this::aimParams).debounce(TURRET_DEBOUNCE, DebounceType.kFalling);
    Trigger shooterReady = subsystems.shooter().tracked(this::aimParams).debounce(SHOOTER_DEBOUNCE, DebounceType.kFalling);
    return aimOk
      .and(turretReady) // Comment out this part to enable drivetrain aiming. Make sure the turret's at zero.
      .and(shooterReady)
      .and(shouldShoot().or(() -> forceWhenReady));
  }

  public Trigger shootReady() {
    return shootReady(false);
  }

  public Trigger shouldAgitate() {
    return forcedShootReady;
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
    SmartDashboard.putBoolean("Robot/Aim OK", aimParams().isOk());
    SmartDashboard.putBoolean("Robot/Shoot Ready", shootReady.getAsBoolean());
    SmartDashboard.putBoolean("Robot/Shoot Ready (forced)", forcedShootReady.getAsBoolean());
        SmartDashboard.putBoolean("Robot/Turret Ready",
        subsystems.turret().tracked(this::aimParams).getAsBoolean());

    SmartDashboard.putBoolean("Robot/Shooter Ready",
        subsystems.shooter().tracked(this::aimParams).getAsBoolean());
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
