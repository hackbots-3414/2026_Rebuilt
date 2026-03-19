package frc.robot.superstructure;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
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

  public enum ShootMode {
    Scoring,
    Feeding,
    Donut; // "donut" shoot sounds like "do not" shoot...
  }

  private ShootMode wantedShootMode;

  private AimParams params = new AimParams(AimStatus.Unchecked);
  private AimParams predictedParams = new AimParams(AimStatus.Unchecked);

  public final Trigger shootReady;

  public StateManager(Subsystems subsystems) {
    this.subsystems = subsystems;
    shootReady = initShootReady();
    OnboardLogger log = new OnboardLogger("Robot");
    log.registerPose("Robot Pose", this::robotPose);
    log.registerTransform2d("Robot Velocity", this::robotVelocity);
    log.registerPose3d("Turret Position", this::turretPose);
    log.registerBoolean("Shoot Ready", shootReady);
    log.registerBoolean("Turret Tracked", subsystems.turret().tracked(this::aimParams));
    log.registerBoolean("Shooter Tracked", subsystems.shooter().tracked(this::aimParams));
    log.registerBoolean("In Alliance Zone", () -> FieldUtils.inAllianceZone(robotPose()));

    OnboardLogger aimParamsLogger = new OnboardLogger("Robot/Aim Params");
    AimParams.setupLogging(aimParamsLogger, () -> params);
    OnboardLogger predictedAimParamsLogger = new OnboardLogger("Robot/Aim Params (Predicted)");
    AimParams.setupLogging(predictedAimParamsLogger, () -> predictedParams);
  }

  private ShootMode calculateWantedShootMode() {
    boolean inAllianceZone = FieldUtils.inAllianceZone(robotPose());
    boolean auto = DriverStation.isAutonomous();

    // Don't automatically feed in auto. Yet.
    if (auto) {
      return inAllianceZone ? ShootMode.Scoring : ShootMode.Donut;
    }

    // If we're feeding, then all requirements have been met. We want to feed.
    if (!inAllianceZone) {
      return ShootMode.Feeding;
    }

    double SHOT_TIME = 4.0;
    boolean willBeActive = ActivityCalculator.is(ActivityCalculator.us())
        || ActivityCalculator.is(ActivityCalculator.other(), SHOT_TIME);

    return willBeActive ? ShootMode.Scoring : ShootMode.Donut;
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

  public Trigger shooting() {
    return subsystems.shooter().shooting;
  }

  public Trigger shooting(ShootMode mode) {
    return shooting().and(() -> wantedShootMode == mode);
  }

  private Trigger initShootReady() {
    final double SHOOTER_DEBOUNCE = 1.5;
    final double TURRET_DEBOUNCE = 0.1;
    final boolean FORCE_ODOMETRY = false;

    Trigger aimOk = new Trigger(() -> aimParams().isOk() && predictedAimParams().isOk());
    Trigger turretReady = subsystems.turret().tracked(this::aimParams).debounce(TURRET_DEBOUNCE, DebounceType.kFalling);
    Trigger shooterReady = subsystems.shooter().tracked(this::aimParams).debounce(SHOOTER_DEBOUNCE,
        DebounceType.kFalling);
    Trigger validOdometry = subsystems.drivetrain().validOdemetry();
    return aimOk
        .and(turretReady)
        .and(shooterReady)
        .and(validOdometry.or(() -> !FORCE_ODOMETRY));
  }

  public Trigger shouldAgitate() {
    return shootReady;
  }

  public AimParams aimParams() {
    if (wantedShootMode == ShootMode.Donut) {
      params = AimParams.impossible();
    }

    if (params.status == AimStatus.Unchecked) {
      Translation2d velocity = robotVelocity().getTranslation();
      Pose3d turret = turretPose();

      params = switch (wantedShootMode) {
        case Donut -> AimParams.impossible();
        case Scoring -> AimConstants.kAim.update(FieldUtils.hub(), turret, velocity);
        case Feeding -> AimConstants.kAim.update(FieldUtils.feedTarget(robotPose()), turret, velocity);
      };
    }

    return params;
  }

  public AimParams predictedAimParams() {
    // if (wantedShootMode == ShootMode.Donut) {
    //   predictedParams = AimParams.impossible();
    // }

    // if (predictedParams.status == AimStatus.Unchecked) {
    //   Pose2d predictedPose = subsystems.drivetrain().predictedRobotPose();
    //   Translation2d predictedVelocity = subsystems.drivetrain().predictedRobotVelocity();
    //   Pose3d predictedTurret = subsystems.turret().turretPose(predictedPose);

    //   if (wantedShootMode == ShootMode.Scoring) {
    //     predictedParams = AimConstants.kAim.update(FieldUtils.hub(), predictedTurret, predictedVelocity);
    //   } else {
    //     predictedParams = AimConstants.kAim.update(FieldUtils.feedTarget(predictedPose), predictedTurret, predictedVelocity);
    //   }
    // }

    return aimParams(); // For now, don't use predictions.
  }

  public void update() {
    params = new AimParams(AimStatus.Unchecked);
    predictedParams = new AimParams(AimStatus.Unchecked);

    wantedShootMode = calculateWantedShootMode();

    params = aimParams();
    predictedParams = predictedAimParams();
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

  public Trigger intaking() {
    return subsystems.intake().intaking();
  }

}
