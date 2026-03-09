package frc.robot.vision.localization;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Milliseconds;
import java.util.Set;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.CameraTrustConfig;

public class LocalizationConstants {
  public static final CameraConfig kRegularBaseCameraConfig = new CameraConfig(
      "", // Name
      480, // Resolution height
      640, // Resolution width
      Degrees.of(92), // Diagonal FOV
      Degrees.of(77.4), // Horizontal FOV
      () -> Transform3d.kZero, // Pose supplier
      new CameraTrustConfig(
          VecBuilder.fill(0.3, 0.3, 0.3), // Base std devs
          0.75, // Latency threshold
          1.3, // Latency multiplier
          2.5, // Field XY margin
          1.5, // Field Z margin
          0.8, // Noisy distance
          70.0, // Distance multiplier
          4.5, // Distance max
          0.2, // Ambiguity threshold
          5, // Ambiguity multiplier
          0.2, // Ambiguity shifter
          80, // Target divisor
          0.1, // Difference threshold
          20)); // Difference multiplier

  protected static final AprilTagFieldLayout kTagLayout =
      AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);

  protected static final String kEstimationName = "estimation";
  protected static final String kRejectName = "rejected";

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
  protected static final int kResHeight = 480;

  // Simulated error:
  protected static final Time kAvgLatency = Milliseconds.of(18);
  protected static final Time kLatencyStdDev = Milliseconds.of(2);
  protected static final double kAvgErr = 0.08;
  protected static final double kErrStdDevs = 0.02;

  public static final boolean kEnableTagFilter = false;

  protected static final Set<Integer> kApprovedTagIds = Set.of(2, 3, 4, 5, 8, 9, 10, 11);

  public static final Transform3d kTurretAoRToTurretCameraOffset =
      new Transform3d(0.064, -0.02, 0.038, new Rotation3d(0, Units.degreesToRadians(-30), 0));

  /** Maximum time since last pose estimate for odometry to be considered valid */
  public static final double kValidOdometryCutoff = 0.5;

  public static final boolean kUsePnPDistanceTrigSolve = false;
}
