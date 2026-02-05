
package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;

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
      io.setAngle(params.get().yaw.getMeasure());
      io.setVelocity(RadiansPerSecond.of(params.get().velocity.in(MetersPerSecond) * ShooterConstants.kSpeedTransferPercentage * ShooterConstants.kRadius.in(Meters)));
    });
  }

  public Command reverse() {
    return this.startEnd(() -> io.setVelocity(ShooterConstants.kReverseVelocity), () -> io.setVelocity(RadiansPerSecond.zero()));
  }
}
