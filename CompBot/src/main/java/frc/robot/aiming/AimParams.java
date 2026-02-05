package frc.robot.aiming;

import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.LinearVelocity;

/**
 * The parameters of a shooter at a particlar moment in time. This is the type that is returned when
 * a shot is generated, meaning that this is what is applied to each subystem.
 */
public class AimParams {
  /** The status of the parameters object. */
  public AimStatus status = AimStatus.Unchecked;
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
  public Rotation2d deltaYaw = Rotation2d.fromDegrees(2);
  /** the tolerated error in the shot's velocity */
  public LinearVelocity deltaVelocity = MetersPerSecond.of(0.05);

  public enum AimStatus {
    /** The program has not yet evaluated the valididty of this parameters object */
    Unchecked,
    /** Not a possible shot, do not try to attempt. Values are invalid. */
    Impossible,
    /** A shot that is calculated to go in */
    Possible;

    public boolean isOk() {
      return this == Possible;
    }
  }

  public static final AimParams kImpossible = new AimParams().withStatus(AimStatus.Impossible);

  /** Returns whether the aim parameters calculated are feasible */
  public boolean isOk() {
    return status.isOk();
  }

  public AimParams withStatus(AimStatus status) {
    this.status = status;
    return this;
  }
}

