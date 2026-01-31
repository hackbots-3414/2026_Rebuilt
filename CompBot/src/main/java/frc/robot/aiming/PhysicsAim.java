package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.superstructure.StateManager;

public class PhysicsAim extends AimStrategy {
  private final double finalDescentSpeed;
  private final double maxShooterVelocity;

  private final Rotation2d minShooterAngle;
  private final Rotation2d maxShooterAngle;

  public PhysicsAim(double finalDescentSpeed, double minShooterAngle, double maxShooterAngle) {
    if (finalDescentSpeed <= 0) {
      finalDescentSpeed = 1.0;
      DriverStation.reportWarning(
          "Positive final descent speed required, but nonpositive number found; assigning value of 1.",
          false);
    }
    this.finalDescentSpeed = finalDescentSpeed;
    this.minShooterAngle = Rotation2d.fromDegrees(minShooterAngle);
    this.maxShooterAngle = Rotation2d.fromDegrees(maxShooterAngle);
    this.maxShooterVelocity = Double.POSITIVE_INFINITY;
  }

  public AimParams update(StateManager state) {
    params.status = AimStatus.Impossible;

    Translation3d offset = state.aimTarget().getTranslation()
        .minus(state.turretPose().getTranslation());

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
    Translation2d robotVelocity = state.robotVelocity().getTranslation();
    vx -= robotVelocity.getX();
    vy -= robotVelocity.getY();

    double v = Math.sqrt(vx * vx + vy * vy + vz * vz);
    Rotation2d yaw = Rotation2d.fromRadians(Math.atan2(vy, vx));
    Rotation2d pitch = Rotation2d.fromRadians(Math.asin(vz / v));

    if (v > maxShooterVelocity) {
      params.status = AimStatus.Impossible;
      return params;
    }

    params.status = AimStatus.Ideal;

    if (pitch.getRadians() < minShooterAngle.getRadians()) {
      pitch = minShooterAngle;
      // Use pitch-constrainted SOTM
      params.status = AimStatus.PitchConstrained;
      Translation2d transformedVelocity =
          transformVelocity(offset.toTranslation2d(), state.robotVelocity().getTranslation());
      double vr = transformedVelocity.getX();
      double dh = Math.hypot(dx, dy);
      double velocity = constrainedSOTM(dh, dz, pitch, vr);
      if (velocity == Double.NaN) {
        params.status = AimStatus.Impossible;
        return params;
      }
      double vh = velocity * pitch.getCos();
      t = dh / (vh + vr);
      Translation2d fuelVelocity =
          offset.toTranslation2d().div(t).minus(state.robotVelocity().getTranslation());
      yaw = Rotation2d.fromRadians(Math.atan2(fuelVelocity.getY(), fuelVelocity.getX()));
      v = velocity;
    }

    params.velocity = MetersPerSecond.of(v);
    params.pitch = pitch;
    params.yaw = yaw;

    return params;
  }

  /** Returns the necessary velocity for some given pitch */
  private double constrainedSOTM(double dx, double dz, Rotation2d pitch, double vr) {
    double a = pitch.getCos() * (dz * pitch.getCos() - dx * pitch.getSin());
    double b = vr * (2 * dz * pitch.getCos() - dx * pitch.getSin());
    double c = dz * vr * vr + 0.5 * 9.81 * dx * dx;

    // Solve the quadratic equation ax^2 + bx + c = 0
    double discriminant = b * b - 4 * a * c;

    double solution = (-b - Math.sqrt(discriminant)) / (2 * a);
    return solution;
  }

  /**
   * Returns the transformed velocity so that +x is towards the target and +y is counterclockwise
   * tangential to the target
   */
  private Translation2d transformVelocity(Translation2d offset, Translation2d velocity) {
    double thetaR = Math.atan2(offset.getY(), offset.getX());
    double thetaV = Math.atan2(velocity.getY(), velocity.getX());
    double theta = thetaR - thetaV;

    double parallel = velocity.getNorm() * Math.sin(theta);
    double perpendicular = velocity.getNorm() * Math.cos(theta);
    return new Translation2d(perpendicular, parallel);
  }
}
