package frc.robot.aiming;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.LinearVelocity;

import frc.robot.util.OnboardLogger;

/**
 * The parameters of a shooter at a particlar moment in time. This is the type that is returned when
 * a shot is generated, meaning that this is what is applied to each subystem.
 */
public class AimParams {
  /** the launch angle of the fuel out of the robot. */
  public Rotation2d pitch = Rotation2d.kZero;
  /**
   * the direction that the fuel should be ejected out at, relative to the robot (but still in the
   * field's coordinate system). In practice, this is basically the field-relative heading of the
   * shooter.
   */
  public Rotation2d yaw = Rotation2d.kZero;
  /** the velocity that the fuel should be ejected out at, relative to the robot. */
  public LinearVelocity velocity = MetersPerSecond.zero();
  /** the tolerated error in the shot's pitch */
  public Rotation2d deltaPitch = Rotation2d.fromDegrees(2);
  /** the tolerated error in the shot's yaw */
  public Rotation2d deltaYaw = Rotation2d.fromDegrees(1);
  /** the tolerated error in the shot's velocity */
  public LinearVelocity deltaVelocity = MetersPerSecond.of(0.05);

  private OnboardLogger ologger = new OnboardLogger("AimParams");

  public AimParams() {
    ologger.registerMeasurment("Pitch", () -> pitch.getMeasure(), Degrees);
    ologger.registerMeasurment("Yaw", () -> yaw.getMeasure(), Degrees);
    ologger.registerMeasurment("Velocity", () -> velocity, MetersPerSecond);
  }
}

