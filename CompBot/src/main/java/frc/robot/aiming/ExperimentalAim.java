package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.LinearVelocity;
import frc.robot.superstructure.StateManager;

public abstract class ExperimentalAim implements AimStrategy {
  /**
   * Predicts the aim parameters for a static shot from the given position. Velocity should NOT be
   * considered in the implementation of this method.
   */
  public abstract AimParams predict(StateManager state, AimParams params);

  public final AimParams update(StateManager state, AimParams params) {
    params = predict(state, params);
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
    LinearVelocity velocity = MetersPerSecond.of(Math.sqrt(vx * vx + vy * vy + vz * vz));
    // Recalculate angles
    Rotation2d yaw = Rotation2d.fromRadians(Math.atan2(vy, vx));
    Rotation2d pitch = Rotation2d.fromRadians(Math.asin(vz / velocity.in(MetersPerSecond)));
    // Reapply parameters
    params.yaw = yaw;
    params.pitch = pitch;
    params.velocity = velocity;
    return params;
  }
}
