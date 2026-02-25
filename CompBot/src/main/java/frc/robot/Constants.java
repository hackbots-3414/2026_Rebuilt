package frc.robot;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Distance;
import frc.robot.aiming.AimConstraints;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;

public class Constants {
  // Checked the FIRST Game Manual and fixed the field dimensions.
  public static class FieldConstants {
    /** The longer side, corresponds to X values */
    public static final Distance kFieldLength = Meters.of(16.541);
    /** The shorter side, corresponds to Y values */
    public static final Distance kFieldWidth = Meters.of(8.069);
    /** The length of the alliance zone, corresponds to X axis */
    public static final Distance kAllianceZoneLength = Inches.of(182.11);

    public static final Pose3d kBlueHub = new Pose3d(4.632516, 4.011139, 1.83, Rotation3d.kZero);
    public static final Pose3d kFeedTarget = new Pose3d(4.5, 2, 1.0, Rotation3d.kZero);
  }

  public static class AimConstants {
    public static final AimStrategy kAim = new PhysicsAim(
        new AimConstraints(
            Rotation2d.fromDegrees(49.5), // Min pitch
            Rotation2d.fromDegrees(72.0), // Max pitch
            16), // Max output (speed)
        2,
        10);
  }
}
