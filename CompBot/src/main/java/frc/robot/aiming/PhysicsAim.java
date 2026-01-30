package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.superstructure.StateManager;

public class PhysicsAim extends AimStrategy {
  private final double finalDescentSpeed;

  public PhysicsAim(double finalDescentSpeed) {
    if (finalDescentSpeed <= 0) {
      finalDescentSpeed = 1.0;
      DriverStation.reportWarning(
          "Positive final descent speed required, but nonpositive number found; assigning value of 1.",
          false);
    }
    this.finalDescentSpeed = finalDescentSpeed;
  }

  public AimParams update(StateManager state) {
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
    double yaw = Math.atan2(vy, vx);
    double pitch = Math.asin(vz / v);

    params.velocity = MetersPerSecond.of(v);
    params.pitch = Rotation2d.fromRadians(pitch);
    params.yaw = Rotation2d.fromRadians(yaw);

    return params;
  }
}
