package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.superstructure.StateManager;

public class PhysicsAim extends AimStrategy {
  private static final int ITERATIONS = 5;

  private final AimConstraints constraints;

  private final double minDescentVelocity;
  private final double maxDescentVelocity;

  public PhysicsAim(AimConstraints constraints, double minDescentVelocity,
      double maxDescentVelocity) {
    this.constraints = constraints;
    this.minDescentVelocity = minDescentVelocity;
    this.maxDescentVelocity = maxDescentVelocity;
  }

  public AimParams update(StateManager state) {
    Translation3d offset = state.aimTarget().getTranslation()
        .minus(state.turretPose().getTranslation());
    Translation2d robotVelocity = state.robotVelocity().getTranslation();

    // Calculate the pitch values for the minimum and maximum possible v_zf values:
    AimParams minParams = quicksolve(offset, robotVelocity, minDescentVelocity);
    AimParams maxParams = quicksolve(offset, robotVelocity, maxDescentVelocity);

    double minPitch = minParams.pitch.getRadians();
    double maxPitch = maxParams.pitch.getRadians();

    boolean solutionExists = minPitch <= constraints.maxShooterAngle().getRadians()
        && maxPitch >= constraints.minShooterAngle().getRadians();

    if (!solutionExists) {
      return AimParams.kImpossible;
    }

    boolean minWorks = constraints.check(minParams);
    if (minWorks) {
      return minParams.withStatus(AimStatus.Possible);
    }

    double lower = minDescentVelocity;
    double upper = maxDescentVelocity;

    AimParams best = AimParams.kImpossible;

    for (int i = 0; i < ITERATIONS; i++) {
      double guess = 0.5 * (lower + upper);
      AimParams output = quicksolve(offset, robotVelocity, guess);
      boolean ok = constraints.check(output);
      if (ok) {
        // We're just optimizing, so we won't stop yet.
        upper = guess;
        best = output.withStatus(AimStatus.Possible);
      }
      double pitch = output.pitch.getRadians();
      if (pitch > constraints.maxShooterAngle().getRadians()) {
        // Too high
        upper = guess;
      }
      if (pitch < constraints.minShooterAngle().getRadians()) {
        // Too low
        lower = guess;
      }
    }

    if (best.status == AimStatus.Impossible) {
      return AimParams.kImpossible;
    }

    // If yaw says to shoot in the wrong direction we don't listen, even if it would work.
    Rotation2d towardsTarget = Rotation2d.fromRadians(Math.atan2(offset.getY(), offset.getX()));
    double diff = MathUtil.angleModulus(Math.abs(towardsTarget.minus(best.yaw).getRadians()));
    if (diff > 0.8 * Math.PI) { // We aren't really pointed at the target.
      return AimParams.kImpossible;
    }

    return best;
  }

  public static AimParams quicksolve(
      Translation3d offset,
      Translation2d robotVelocity,
      double finalDescentSpeed) {

    AimParams params = new AimParams();

    double dx = offset.getX();
    double dy = offset.getY();
    double dz = offset.getZ();

    // Solve the quadratic equation 0 = 0.5g * t^2 + v_f * t - dz for time
    double a = 0.5 * 9.81;
    double b = -finalDescentSpeed;
    double c = -dz;
    double discriminant = b * b - 4 * a * c;
    double t = (-b + Math.sqrt(discriminant)) / (2 * a);

    // Solve for the initial velocity components given the time
    double vx = dx / t;
    double vy = dy / t;
    double vz = 9.81 * t - finalDescentSpeed;

    // Compensate for robot velocity
    vx -= robotVelocity.getX();
    vy -= robotVelocity.getY();

    double v = Math.sqrt(vx * vx + vy * vy + vz * vz);
    Rotation2d yaw = Rotation2d.fromRadians(Math.atan2(vy, vx));
    Rotation2d pitch = Rotation2d.fromRadians(Math.asin(vz / v));

    params.velocity = MetersPerSecond.of(v);
    params.pitch = pitch;
    params.yaw = yaw;

    return params;
  }

}
