package frc.robot.aiming;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.LinearVelocity;

/**
 * A class representing the physical constraints of the shooter.
 */
public record AimConstraints(
    Rotation2d minShooterAngle,
    Rotation2d maxShooterAngle,
    LinearVelocity maxVelocity) {

  /** Returns whether the given aim parameters satisfy this constraint */
  public boolean check(AimParams params) {
    boolean velocityOk =
        params.velocity.baseUnitMagnitude() <= maxVelocity().baseUnitMagnitude();
    boolean pitchOk = (params.pitch.getRadians() >= minShooterAngle.getRadians())
        && (params.pitch.getRadians() <= maxShooterAngle.getRadians());
    return velocityOk && pitchOk;
  }
}
