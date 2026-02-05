package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.LinearVelocity;
import frc.robot.superstructure.StateManager;

/**
 * An empiracally-tuned aim system
 */
public abstract class EmpiricalAim implements AimStrategy {
  private final AimConstraints constraints;

  public EmpiricalAim(AimConstraints constraints) {
    this.constraints = constraints;
  }

  /**
   * Predicts the aim parameters for a static shot from the given position. Velocity should NOT be
   * considered in the implementation of this method.
   */
  public abstract AimParams predict(StateManager state);

  public final AimParams update(StateManager state) {
    AimParams params = predict(state);
    double feulVelocity = params.velocity.in(MetersPerSecond);
    // Break up initial velocity
    double vx = feulVelocity * params.pitch.getCos() * params.yaw.getCos();
    double vy = feulVelocity * params.pitch.getCos() * params.yaw.getSin();
    double vz = feulVelocity * params.pitch.getSin();
    // Compensate for robot velocity
    Translation2d robotVelocity = state.robotVelocity().getTranslation();
    vx -= robotVelocity.getX();
    vy -= robotVelocity.getY();
    // Recalculate velocity
    LinearVelocity velocity = MetersPerSecond.of(Math.clamp(
        Math.sqrt(vx * vx + vy * vy + vz * vz),
        0,
        constraints.maxVelocity().in(MetersPerSecond)));
    // Recalculate angles
    Rotation2d yaw = Rotation2d.fromRadians(Math.atan2(vy, vx));
    Rotation2d pitch = Rotation2d.fromRadians(Math.clamp(
          Math.asin(vz / velocity.in(MetersPerSecond)),
          constraints.minShooterAngle().getRadians(),
          constraints.maxShooterAngle().getRadians()));
    // Apply parameters
    params.yaw = yaw;
    params.pitch = pitch;
    params.velocity = velocity;
    return params;
  }
}
