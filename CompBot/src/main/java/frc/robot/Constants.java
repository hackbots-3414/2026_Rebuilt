package frc.robot;

import static edu.wpi.first.units.Units.Meters;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Distance;

public class Constants {
  // TODO: these are wrong
  public static class FieldConstants {
    public static final Distance kFieldWidth = Meters.of(16.541);
    public static final Distance kFieldLength = Meters.of(8.069);
    
    public static final Pose3d kBlueHub = new Pose3d(4.632516, 4.011139, 3.0, Rotation3d.kZero);
  }
}
