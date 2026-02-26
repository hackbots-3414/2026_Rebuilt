package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.CommandBasedTest;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimParams.SpeedControl;
import frc.robot.subsystems.shooter.ShooterConstants.HoodConstants;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;

public class ShooterTest extends CommandBasedTest {
  @Test
  public void shooterTest() {
    ShooterIO mockShooterIO = mock(ShooterIO.class);

    doAnswer(invocation -> {
      ShooterIOInputs inputs = invocation.getArgument(0, ShooterIOInputs.class);
      inputs.shooter1Velocity = ShooterConstants.kMaxRotationalSpeed;
      inputs.hoodPosition = Degrees.of(35).minus(HoodConstants.kOffset);
      // It's a void method, so we're supposed to return null.
      return null;
    }).when(mockShooterIO).updateInputs(Mockito.any(ShooterIOInputs.class));

    Shooter shooter = new Shooter(mockShooterIO);
    CommandScheduler.getInstance().run();
    // Ensure we update the inputs every periodic run
    verify(mockShooterIO).updateInputs(Mockito.any(ShooterIOInputs.class));

    AimParams params = new AimParams();
    params.pitch = Rotation2d.fromDegrees(55);
    params.output = ShooterConstants.kMaxLinearSpeed.in(MetersPerSecond);
    params.control = SpeedControl.ProjectileVelocity;

    CommandScheduler.getInstance().schedule(shooter.shoot(() -> params));
    CommandScheduler.getInstance().run();
    // These methods should have been called from the running command.
    verify(mockShooterIO).setVelocity(
      ShooterConstants.kMaxRotationalSpeed
        .times(params.output / ShooterConstants.kMaxLinearSpeed.in(MetersPerSecond)),
        false); / 
    verify(mockShooterIO).setAngle(Degrees.of(35).minus(HoodConstants.kOffset));

    CommandScheduler.getInstance().schedule(shooter.reverse());
    CommandScheduler.getInstance().run();
    verify(mockShooterIO).setVelocity(ShooterConstants.kReverseVelocity);

    assertTrue(shooter.tracked(() -> params));
  }
}
