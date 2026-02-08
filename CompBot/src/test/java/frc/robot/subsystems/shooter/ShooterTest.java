package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.CommandBasedTest;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.shooter.ShooterConstants.HoodConstants;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;

public class ShooterTest extends CommandBasedTest {
  @Test
  public void shooterTest() {
    ShooterIO mockShooterIO = mock(ShooterIO.class);
    Shooter shooter = new Shooter(mockShooterIO);
    CommandScheduler.getInstance().run();
    // Ensure we update the inputs every periodic run
    verify(mockShooterIO).updateInputs(Mockito.any(ShooterIOInputs.class));

    AimParams params = new AimParams();
    params.pitch = Rotation2d.fromDegrees(55);
    params.velocity = MetersPerSecond.of(5.0);

    CommandScheduler.getInstance().schedule(shooter.shoot(() -> params));
    CommandScheduler.getInstance().run();
    // These methods should have been called from the running command.
    verify(mockShooterIO).setVelocity(ShooterConstants.kMaxRotationalSpeed
        .times(params.velocity.div(ShooterConstants.kMaxLinearSpeed)));
    verify(mockShooterIO).setAngle(Degrees.of(35).minus(HoodConstants.kOffset));

    CommandScheduler.getInstance().schedule(shooter.reverse());
    CommandScheduler.getInstance().run();
    verify(mockShooterIO).setVelocity(ShooterConstants.kReverseVelocity);
  }
}
