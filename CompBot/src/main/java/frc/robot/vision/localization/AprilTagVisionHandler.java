package frc.robot.vision.localization;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import frc.robot.Robot;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.CameraIO;
import frc.robot.vision.CameraIOHardware;

public class AprilTagVisionHandler implements AutoCloseable {
  @SuppressWarnings("unused")
  private final Logger m_logger = LoggerFactory.getLogger(AprilTagVisionHandler.class);

  private final Notifier m_notifier;
  private final List<SingleInputPoseEstimator> m_estimators = new ArrayList<>();
  private final MultiInputFilter m_filter;

  private final Superstructure superstructure;

  public AprilTagVisionHandler(Superstructure superstructure, List<CameraConfig> configs) {
    this.superstructure = superstructure;
    m_filter = new MultiInputFilter();
    setupCameras(configs);
    m_notifier = new Notifier(this::updateEstimators);
  }

  private void setupCameras(List<CameraConfig> configs ) {
    for (CameraConfig config: configs) {
      CameraIO io;
      if (Robot.isSimulation()) {
        io = new CameraIOAprilTagSim(config, superstructure.state::robotPose);
      } else {
        io = new CameraIOHardware(config);
      }
      SingleInputPoseEstimator estimator = new SingleInputPoseEstimator(
          superstructure,
          m_filter,
          io,
          config,
          this::addEstimate);
      m_estimators.add(estimator);
    }
  }

  public void updateEstimators() {
    m_filter.clear();
    for (SingleInputPoseEstimator estimator : m_estimators) {
      estimator.refresh(superstructure.state.robotPose());
    }

    for (SingleInputPoseEstimator estimator : m_estimators) {
      estimator.run();
    }
  }

  public void startThread() {
    m_notifier.startPeriodic(LocalizationConstants.kPeriodic);
  }

  private void addEstimate(TimestampedPoseEstimate estimate) {
    // if (DriverStation.isDisabled()) {
    //   if (!m_filter.verify(estimate.pose())) {
    //     return;
    //   }
    // }
    superstructure.addPoseEstimate(estimate);
  }

  @Override
  public void close() {
    m_notifier.close();
  }
}
