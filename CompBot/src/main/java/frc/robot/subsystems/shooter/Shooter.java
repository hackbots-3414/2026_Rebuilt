
package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;

import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;

/** Shooter subsystem. */
public class Shooter extends SubsystemBase {
  private final ShooterIO io;
  private final ShooterIOInputs inputs = new ShooterIOInputs();

  public Shooter(ShooterIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  public Command shoot(Supplier<AimParams> params) {
    return this.run(() -> {
      // The hood's angle is normal to the angle of the ball, i.e. the hood angle is 90 degrees
      // minus the pitch.
      Angle hoodAngle = Rotation2d.kCCW_90deg.minus(params.get().pitch).getMeasure();
      // Assume linear relationship between shooter rotational speed and projectile linear speed.
      AngularVelocity shooterSpeed = ShooterConstants.kMaxRotationalSpeed.times(
          params.get().velocity.div(ShooterConstants.kMaxLinearSpeed));
      io.setAngle(hoodAngle);
      io.setVelocity(shooterSpeed);
    });
  }

  public Command reverse() {
    return this.startEnd(() -> io.setVelocity(ShooterConstants.kReverseVelocity),
        () -> io.setVelocity(RadiansPerSecond.zero()));
  }
}
