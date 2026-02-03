package frc.robot.aiming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.aiming.AimParams.AimStatus;

public class PhysicsAimTest {
  @Test
  public void setStatusTest() {
    AimParams params = new AimParams();
    assertEquals(params.status, AimStatus.Unchecked);
    params = params.withStatus(AimStatus.Possible);
    assertEquals(params.status, AimStatus.Possible);
    assertTrue(params.isOk());
    params = params.withStatus(AimStatus.Impossible);
    assertEquals(params.status, AimStatus.Impossible);
  }

  @Test
  public void ensureMonotonic() {
    Translation3d offset = new Translation3d(1,1,3);
    Translation2d velocity = Translation2d.kZero;
    double last = Double.NEGATIVE_INFINITY;
    for (double vz = 0.0; vz <= 5.0; vz += 0.1) {
      AimParams params = PhysicsAim.quicksolve(offset, velocity, vz);
      assertTrue(params.pitch.getDegrees() > last);
      last = params.pitch.getDegrees();
    }
  }

  @Test
  public void testAngleRounding() {
    Rotation2d angle1 = Rotation2d.fromDegrees(180);
    Rotation2d angle2 = Rotation2d.fromDegrees(-180);
    Rotation2d difference = angle1.minus(angle2);
    double diff = MathUtil.angleModulus(difference.getRadians());
    System.out.println(diff);
    assertTrue(Math.abs(diff) < 1e-3);
  }
}
