
package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.shooter.ShooterConstants.HoodConstants;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;
import frc.robot.util.OnboardLogger;

/** Shooter subsystem. */
public class Shooter extends SubsystemBase {
  private final ShooterIO io;
  private final ShooterIOInputs inputs = new ShooterIOInputs();

  private Angle hoodReference = Rotations.zero();
  private AngularVelocity shooterReference = RotationsPerSecond.zero();

  public Shooter(ShooterIO io) {
    this.io = io;

    // When the robot is disabled, set velocity back to zero. This way, when it starts up again, it
    // won't try to go all the way back to the last commanded velocity.
    RobotModeTriggers.disabled().onTrue(
        this.runOnce(() -> io.setVelocity(RotationsPerSecond.zero())).ignoringDisable(true));

    OnboardLogger log = new OnboardLogger("Shooter");
    log.registerMeasurement("Hood Reference", () -> hoodReference, Rotations);
    log.registerMeasurement("Velocity Reference", () -> shooterReference, RotationsPerSecond);
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  private AngularVelocity projectileToShooterVelocity(double projectileVelocity) {
    // Assume linear relationship between shooter rotational speed and projectile linear speed.
    return ShooterConstants.kMaxRotationalSpeed
        .times(projectileVelocity / ShooterConstants.kMaxLinearSpeed.in(MetersPerSecond));
  }

  private Angle pitchToHoodAngle(Rotation2d pitch) {
    // The hood's angle is normal to the angle of the ball, i.e. the hood angle is 90 degrees
    // minus the pitch. Then we subtract the offset to get the mechanism position.
    return Rotation2d.kCCW_90deg.minus(pitch).getMeasure().minus(HoodConstants.kOffset);
  }

  /**
   * This command tells the shooter to begin to track the desired target. When this command ends, it
   * does NOT turn off the shooter; we don't want to have to get all the way back up to speed.
   */
  public Command shoot(Supplier<AimParams> params) {
    return this.run(() -> {
      shooterReference = projectileToShooterVelocity(params.get().velocity);
      hoodReference = pitchToHoodAngle(params.get().pitch);
      io.setVelocity(shooterReference);
      io.setAngle(hoodReference);
    });
  }

  /** Runs the shooter in reverse, to potentially remove any jams. */
  public Command reverse() {
    return this.startEnd(
        () -> io.setVelocity(ShooterConstants.kReverseVelocity),
        () -> io.setVelocity(RadiansPerSecond.zero()));
  }

  public Trigger tracked(Supplier<AimParams> params) {
    return new Trigger(() -> {
      double velocityError = inputs.shooter1Velocity
          .minus(projectileToShooterVelocity(params.get().velocity)).baseUnitMagnitude();
      boolean velocityOk =
          Math.abs(velocityError) <= params.get().deltaVelocity;

      double hoodError =
          inputs.hoodPosition.minus(pitchToHoodAngle(params.get().pitch)).baseUnitMagnitude();
      boolean hoodOk =
          Math.abs(hoodError) <= params.get().deltaPitch.getMeasure().baseUnitMagnitude();
      return velocityOk && hoodOk;
    });
  }
}
