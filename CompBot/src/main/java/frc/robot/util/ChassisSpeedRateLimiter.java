package frc.robot.util;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public class ChassisSpeedRateLimiter {
  private ChassisSpeeds last;
  private double lastTime;

  private final double accelerationLimit;
  private final double angularAccelerationLimit;

  public ChassisSpeedRateLimiter(double accelerationLimit, double angularAccelerationLimit) {
    this.accelerationLimit = accelerationLimit;
    this.angularAccelerationLimit = angularAccelerationLimit;
  }

  public ChassisSpeeds calculate(ChassisSpeeds input) {
    // Time differences
    double currentTime = Timer.getTimestamp();
    double dt = currentTime - lastTime;
    // Cap 2d acceleration
    double dx = input.vxMetersPerSecond - last.vxMetersPerSecond;
    double dy = input.vyMetersPerSecond - last.vyMetersPerSecond;
    double domega = MathUtil.angleModulus(input.omegaRadiansPerSecond - last.omegaRadiansPerSecond);
    double hypot = Math.hypot(dx, dy);
    if (hypot > accelerationLimit * dt) {
      // divide by hypotenuse to normalize, then multiply by new length
      dx *= accelerationLimit * dt / hypot;
      dy *= accelerationLimit * dt / hypot;
    }
    domega = MathUtil.clamp(
        domega,
        -angularAccelerationLimit * dt,
        angularAccelerationLimit * dt);
    last = new ChassisSpeeds(
        last.vxMetersPerSecond + dx,
        last.vyMetersPerSecond + dy,
        last.omegaRadiansPerSecond + domega);
    lastTime = currentTime;
    return last;
  }

  public void reset(ChassisSpeeds seed) {
    last = seed;
  }
}
