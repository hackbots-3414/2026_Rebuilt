package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.CommandBasedTest;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.subsystems.turret.TurretIO.TurretIOInputs;
import frc.robot.superstructure.StateManager;

public class TurretTest extends CommandBasedTest {
  @Test
  public void turretTest() {
    TurretIO mockIO = mock(TurretIO.class);

    Turret turret = new Turret(mockIO);
    verify(mockIO).calibrate();

    CommandScheduler.getInstance().run();
    verify(mockIO).updateInputs(Mockito.any(TurretIOInputs.class));

    CommandScheduler.getInstance().schedule(turret.forwards());
    verify(mockIO).setPosition(TurretConstants.kForwards);

    StateManager mockState = mock(StateManager.class);
    AimParams params = new AimParams().withStatus(AimStatus.Possible);
    params.yaw = Rotation2d.fromDegrees(34.14);
    when(mockState.aimParams()).thenReturn(params);
    when(mockState.robotPose()).thenReturn(new Pose2d(Translation2d.kZero, Rotation2d.fromDegrees(10.0)));
    when(mockState.shootReady()).thenReturn(new Trigger(() -> false));

    CommandScheduler.getInstance().schedule(turret.track(mockState));
    CommandScheduler.getInstance().run();

    // The turret should have adjusted to the robot's heading to make sure that the field-relative
    // angle is correct.
    verify(mockIO).setPosition(Degrees.of(24.14).plus(TurretConstants.kForwards));
  }

  @Test
  public void angleWrapTest() {
    double[][] testCases = {
      {0, 0, -0.75, 0.75, 0},
      {0, 1.1, 0, 2, 0.1},
      {1, 0.9, 0.95, 2, 1.9}};

    for (double[] testCase: testCases) {
      double x, y, min, max, expected;
      x = testCase[0];
      y = testCase[1];
      min = testCase[2];
      max = testCase[3];
      expected = testCase[4];

      double out = Turret.findCC(x, y, min, max);
      assertTrue(Math.abs(out - expected) < 1e-3);
    }
  }

}
