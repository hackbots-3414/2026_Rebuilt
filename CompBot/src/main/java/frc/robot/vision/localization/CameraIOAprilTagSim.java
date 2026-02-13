package frc.robot.vision.localization;

import static edu.wpi.first.units.Units.Milliseconds;

import java.util.function.Supplier;

import org.photonvision.PhotonCamera;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.FieldManager;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.CameraIO;

public class CameraIOAprilTagSim implements CameraIO {
  private static final VisionSystemSim simSystem = new VisionSystemSim("localization");

  private static final SimCameraProperties simProps = new SimCameraProperties();

  private static final Field2d simField = simSystem.getDebugField();

  private final PhotonCamera m_camera;
  private final PhotonCameraSim m_cameraSim;

  private final Supplier<Pose2d> m_poseSupplier;

  private final CameraConfig config;

  public CameraIOAprilTagSim(CameraConfig config, Supplier<Pose2d> poseSupplier) {
    FieldManager.getInstance().setField(simField);
    setupSimProps();
    this.config = config;
    m_poseSupplier = poseSupplier;
    m_camera = new PhotonCamera(config.cameraName());
    m_cameraSim = new PhotonCameraSim(m_camera, simProps);
    m_cameraSim.enableDrawWireframe(true);
    simSystem.addCamera(m_cameraSim, config.robotToCamera().get());
    SmartDashboard.putBoolean("Vision/" + config.cameraName() + " connected", true);
  }

  public void updateInputs(CameraIOInputs inputs) {
    inputs.connected =
        SmartDashboard.getBoolean("Vision/" + config.cameraName() + " connected", true);
    simSystem.update(m_poseSupplier.get());
    inputs.unreadResults = m_camera.getAllUnreadResults();
  }

  private void setupSimProps() {
    simSystem.addAprilTags(LocalizationConstants.kTagLayout);
    simProps.setCalibration(
        config.resolutionWidth(),
        config.resolutionHeight(),
        new Rotation2d(config.fov()));
    simProps.setAvgLatencyMs(LocalizationConstants.kAvgLatency.in(Milliseconds));
    simProps.setLatencyStdDevMs(LocalizationConstants.kLatencyStdDev.in(Milliseconds));
    simProps.setCalibError(LocalizationConstants.kAvgErr, LocalizationConstants.kErrStdDevs);
  }
}
