package frc.robot.vision.localization;

import static edu.wpi.first.units.Units.Seconds;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ctre.phoenix6.Utils;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.FieldConstants;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.CameraIO;
import frc.robot.vision.CameraIO.CameraIOInputs;

public class SingleInputPoseEstimator implements Runnable {
  private final Logger logger = LoggerFactory.getLogger(SingleInputPoseEstimator.class);

  private Superstructure superstructure;

  private final CameraIO io;
  private final CameraIOInputs inputs;

  private final Consumer<TimestampedPoseEstimate> reporter;
  private Pose2d lastPose;

  private final PhotonPoseEstimator estimator;
  private final MultiInputFilter filter;

  private final Alert disconnectedAlert;

  private final String name;
  private final Transform3d robotToCamera;

  public SingleInputPoseEstimator(
      Superstructure superstructure,
      MultiInputFilter filter,
      CameraIO io,
      String name,
      Transform3d robotToCamera,
      Consumer<TimestampedPoseEstimate> updateCallback) {
    this.superstructure = superstructure;
    this.io = io;
    this.name = name;
    inputs = new CameraIOInputs();
    reporter = updateCallback;
    this.robotToCamera = robotToCamera;
    this.filter = filter;
    disconnectedAlert =
        new Alert("Vision/Camera Status", name + " disconnected", AlertType.kError);
    estimator = new PhotonPoseEstimator(LocalizationConstants.kTagLayout,robotToCamera);
  }

  public void refresh(Pose2d robotPose) {
    lastPose = robotPose;
    io.updateInputs(inputs);
    disconnectedAlert.set(!inputs.connected);
    if (!inputs.connected) {
      return;
    }
    for (PhotonPipelineResult result : inputs.unreadResults) {
      Set<Integer> tags = result.getTargets().stream()
          .map(target -> target.getFiducialId())
          .collect(Collectors.toSet());
      filter.addInput(name, tags);
    }
  }

  @Override
  public void run() {
    if (!inputs.connected) {
      return;
    }
    // Pull the latest data from the camera.
    List<PhotonPipelineResult> results = inputs.unreadResults;
    estimator.addHeadingData(
        RobotController.getMeasureTime().in(Seconds),
        lastPose.getRotation());
    /* take many */
    for (PhotonPipelineResult result : results) {
      combinedHandleResult(result);
    }
  }

  private void combinedHandleResult(PhotonPipelineResult result) {
    // some prechecks before we do anything
    if (!precheckValidity(result)) {
      return;
    }
    // we can now assume that we have targets
    List<PhotonTrackedTarget> targets = result.getTargets();
    // use solvePnP every time if we can
    Optional<EstimatedRobotPose> est = estimator.estimateCoprocMultiTagPose(result);
    if (est.isEmpty()) {
        est = estimator.estimatePnpDistanceTrigSolvePose(result);
    }
    if (est.isEmpty()) {
        est = estimator.estimateLowestAmbiguityPose(result);
    }
    // Now we are out of options
    if (est.isPresent()) {
      Pose3d estimatedPose = est.get().estimatedPose;
      process(result, estimatedPose).ifPresent(reporter);
    }
    PhotonTrackedTarget target = targets.get(0);
    int fidId = target.getFiducialId();
    Optional<Pose3d> targetPosition = LocalizationConstants.kTagLayout
        .getTagPose(fidId);
    if (targetPosition.isEmpty()) {
      logger.error("Tag {} detected not in field layout", fidId);
      return;
    }

    Pose3d targetPosition3d = targetPosition.get();
    Transform3d best3d = target.getBestCameraToTarget();
    Transform3d alt3d = target.getAlternateCameraToTarget();
    Pose3d best = targetPosition3d
        .plus(best3d.inverse())
        .plus(robotToCamera.inverse());
    Pose3d alt = targetPosition3d
        .plus(alt3d.inverse())
        .plus(robotToCamera.inverse());
    // final decision maker
    double bestHeading = best.getRotation().getZ();
    double altHeading = alt.getRotation().getZ();
    Pose2d pose = lastPose;
    double heading = pose.getRotation().getRadians();
    Transform2d bestDiff = best.toPose2d().minus(pose);
    Transform2d altDiff = alt.toPose2d().minus(pose);
    double bestRotErr = Math.abs(bestHeading - heading);
    double altRotErr = Math.abs(altHeading - heading);
    double bestXYErr = bestDiff.getTranslation().getNorm();
    double altXYErr = altDiff.getTranslation().getNorm();
    Pose3d estimate;

    if (Math.abs(bestRotErr - altRotErr) >= LocalizationConstants.kHeadingThreshold) {
      estimate = (bestRotErr <= altRotErr) ? best : alt;
    } else {
      estimate = (bestXYErr <= altXYErr) ? best : alt;
    }

    process(result, estimate).ifPresent(reporter);
  }

  private boolean precheckValidity(PhotonPipelineResult result) {
    double latency = result.metadata.getLatencyMillis() * 1e-3;
    if (latency > LocalizationConstants.kLatencyThreshold) {
      logger.warn("({}) Refused old vision data, latency of {}", name, latency);
      return false;
    }
    // Ensure we only accept reef-focused estimates
    return result.hasTargets()
        && (!LocalizationConstants.kEnableTagFilter
            || LocalizationConstants.kApprovedTagIds.contains(result.getBestTarget().getFiducialId()));
  }

  private Optional<TimestampedPoseEstimate> process(PhotonPipelineResult result, Pose3d pose) {
    double latency = result.metadata.getLatencyMillis() / 1.0e+3;
    double timestamp = Utils.getCurrentTimeSeconds() - latency;
    double ambiguity = getAmbiguity(result);
    Pose2d flatPose = pose.toPose2d();
    Matrix<N3, N1> stdDevs = calculateStdDevs(result, flatPose);

    // check validity
    if (!checkValidity(pose, ambiguity)) {
      return Optional.empty();
    }
    return Optional.of(
        new TimestampedPoseEstimate(flatPose, timestamp, stdDevs));
  }

  private boolean checkValidity(
      Pose3d pose,
      double ambiguity) {
    if (ambiguity >= LocalizationConstants.kAmbiguityThreshold) {
      return false;
    }
    return !isOutsideField(pose);
  }

  private boolean isOutsideField(Pose3d pose) {
    double x = pose.getX();
    double y = pose.getY();
    double z = pose.getZ();
    double xMax = LocalizationConstants.kXYMargin.magnitude()
        + FieldConstants.kFieldLength.magnitude();
    double yMax = LocalizationConstants.kXYMargin.magnitude()
        + FieldConstants.kFieldWidth.magnitude();
    double xyMin = -LocalizationConstants.kXYMargin.magnitude();
    double zMax = LocalizationConstants.kZMargin.magnitude();
    double zMin = -LocalizationConstants.kZMargin.magnitude();
    return x < xyMin
        || x > xMax
        || y < xyMin
        || y > yMax
        || z > zMax
        || z < zMin;
  }

  private Matrix<N3, N1> calculateStdDevs(PhotonPipelineResult result, Pose2d pose) {
    double latency = result.metadata.getLatencyMillis() * 1e-3;
    double multiplier = calculateStdDevMultiplier(result, latency, pose);
    return LocalizationConstants.kBaseStdDevs.times(multiplier);
  }

  private double calculateStdDevMultiplier(
      PhotonPipelineResult result,
      double latency,
      Pose2d pose) {
    double averageTagDistance = 0;
    for (PhotonTrackedTarget tag : result.getTargets()) {
      averageTagDistance += tag
          .getBestCameraToTarget()
          .getTranslation()
          .getNorm();
    }
    averageTagDistance /= result.getTargets().size();
    // calculate tag distance factor
    double distanceFactor = Math.max(1,
        LocalizationConstants.kDistanceMultiplier
            * (averageTagDistance - LocalizationConstants.kNoisyDistance));
    // calculate an (average) ambiguity real quick:
    double ambiguity = getAmbiguity(result);
    // ambiguity factor
    double ambiguityFactor = Math.max(1,
        LocalizationConstants.kAmbiguityMultiplier * ambiguity
            + LocalizationConstants.kAmbiguityShifter);
    // tag divisor
    double tags = result.getTargets().size();
    double tagDivisor = 1 + (tags - 1) * LocalizationConstants.kTargetMultiplier;
    // distance from last pose
    double poseDifferenceError = Math.max(0,
        lastPose.minus(pose).getTranslation().getNorm()
            - LocalizationConstants.kDifferenceThreshold * superstructure.state.robotVelocity().getTranslation().getNorm());
    double diffMultiplier = Math.max(1,
        poseDifferenceError * LocalizationConstants.kDifferenceMultiplier);
    double timeMultiplier = Math.max(1, latency * LocalizationConstants.kLatencyMultiplier);
    // final calculation
    double stdDevMultiplier = ambiguityFactor
        * distanceFactor
        * diffMultiplier
        * timeMultiplier
        / tagDivisor;
    return stdDevMultiplier;
  }

  private double getAmbiguity(PhotonPipelineResult result) {
    return result.getBestTarget().getPoseAmbiguity();
  }

  public boolean isConnected() {
    return inputs.connected;
  }
}
