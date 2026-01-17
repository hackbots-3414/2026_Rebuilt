package frc.robot.vision.localization;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Milliseconds;
import java.util.Map;
import java.util.Set;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.spline.PoseWithCurvature;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;

public class LocalizationConstants {
  protected static final double kRotationCoefficient = Math.PI * 0.5;
  protected static final double kTranslationCoefficient = 0.06;

  protected static final Vector<N3> kBaseStdDevs =
      VecBuilder.fill(kTranslationCoefficient, kTranslationCoefficient, kRotationCoefficient);

  protected static final AprilTagFieldLayout kTagLayout =
      AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);

  protected static final String kEstimationName = "estimation";
  protected static final String kRejectName = "rejected";

  private static final double kCameraHeight = 0.190;
  private static final double kHorizontalOffset = 0.25; // meters
  private static final double kLowPitch = Units.degreesToRadians(-15);
  private static final double kHighPitch = Units.degreesToRadians(-35);

  private static final double yawOffset = 45;

  public static Map<String, Transform3d> kCameras = Map.ofEntries(
    Map.entry("cam1", new Transform3d(kHorizontalOffset, kHorizontalOffset, kCameraHeight, new Rotation3d(0, kLowPitch, Units.degreesToRadians(-90 + yawOffset)))),
    Map.entry("cam2", new Transform3d(kHorizontalOffset, kHorizontalOffset, kCameraHeight, new Rotation3d(0, kHighPitch, Units.degreesToRadians(180-yawOffset)))),
    Map.entry("cam3", new Transform3d(-kHorizontalOffset, kHorizontalOffset, kCameraHeight, new Rotation3d(0, kLowPitch, Units.degreesToRadians(yawOffset)))),
    Map.entry("cam4", new Transform3d(-kHorizontalOffset, kHorizontalOffset, kCameraHeight, new Rotation3d(0, kHighPitch, Units.degreesToRadians(-180+yawOffset)))),
    Map.entry("cam5", new Transform3d(-kHorizontalOffset, -kHorizontalOffset, kCameraHeight, new Rotation3d(0, kLowPitch, Units.degreesToRadians(180-yawOffset)))),
    Map.entry("cam6", new Transform3d(-kHorizontalOffset, -kHorizontalOffset, kCameraHeight, new Rotation3d(0, kHighPitch, Units.degreesToRadians(-90+yawOffset)))),
    Map.entry("cam7", new Transform3d(kHorizontalOffset, -kHorizontalOffset, kCameraHeight, new Rotation3d(0, kLowPitch, Units.degreesToRadians(-180+yawOffset)))),
    Map.entry("cam8", new Transform3d(kHorizontalOffset, -kHorizontalOffset, kCameraHeight, new Rotation3d(0, kHighPitch, Units.degreesToRadians(yawOffset))))
  );

  /** The tick time for each pose estimator to run */
  protected static final double kPeriodic = 0.02;
  /** The maximum tolerated latency, in seconds. */
  protected static final double kLatencyThreshold = 0.75;
  /** The maximum tolerated ambiguity value. */
  protected static final double kAmbiguityThreshold = 0.2;
  /** The farthest out off a field a pose estimate can say we are (in each dimension separately) */
  protected static final Distance kXYMargin = Meters.of(0.5);
  /** The maximum height from that a camera's pose can reasonably report */
  protected static final Distance kZMargin = Meters.of(1.5);

  // Some configuration variables:
  protected static final double kDistanceMultiplier = 5.0;
  protected static final double kNoisyDistance = 0.8;
  protected static final double kAmbiguityMultiplier = 0.4;
  protected static final double kAmbiguityShifter = 0.2;
  protected static final double kTargetMultiplier = 80;
  protected static final double kDifferenceThreshold = 0.10;
  protected static final double kDifferenceMultiplier = 200.0;
  protected static final double kLatencyMultiplier = 1.3;

  protected static final double kHeadingThreshold = Units.degreesToRadians(3);

  // Stats about the camera for simulation
  protected static final int kResWidth = 640;
  protected static final int kResHeight = 380;
  protected static final Rotation2d kFOV = Rotation2d.fromDegrees(82.0);
  protected static final Rotation2d kHorizontalFov = Rotation2d.fromDegrees(70.0);


  // Simulated error:
  protected static final Time kAvgLatency = Milliseconds.of(18);
  protected static final Time kLatencyStdDev = Milliseconds.of(2);
  protected static final double kAvgErr = 0.08;
  protected static final double kErrStdDevs = 0.02;

  public static final boolean kEnableTagFilter = true;

  protected static final Set<Integer> kApprovedTagIds = Set.of(
      6, 7, 8, 9, 10, 11, // red reef tags
      17, 18, 19, 20, 21, 22, // blue reef tags
      3, 16 // processor tags are okay as well.
  );
}