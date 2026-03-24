package frc.robot.subsystems.turret;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.CommandBasedTest;
import frc.robot.subsystems.turret.TurretIO.TurretIOInputs;

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
