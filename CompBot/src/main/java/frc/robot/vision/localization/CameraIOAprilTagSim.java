package frc.robot.vision.localization;

import static edu.wpi.first.units.Units.Milliseconds;
import java.util.function.Supplier;
import org.photonvision.PhotonCamera;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.FieldManager;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.CameraIO;

public class CameraIOAprilTagSim implements CameraIO {
  private static final VisionSystemSim simSystem = new VisionSystemSim("localization");

  private static final SimCameraProperties simProps = new SimCameraProperties();

  private static final Field2d simField = simSystem.getDebugField();

  private final PhotonCamera camera;
  private final PhotonCameraSim cameraSim;

  private final Supplier<Pose2d> poseSupplier;

  private final CameraConfig config;

  public CameraIOAprilTagSim(CameraConfig config, Supplier<Pose2d> poseSupplier) {
    FieldManager.getInstance().setField(simField);
    this.config = config;
    setupSimProps();
    this.poseSupplier = poseSupplier;
    camera = new PhotonCamera(config.cameraName());
    cameraSim = new PhotonCameraSim(camera, simProps);
    cameraSim.enableDrawWireframe(true);
    simSystem.addCamera(cameraSim, config.robotToCamera().get());
    // SmartDashboard.putBoolean("Vision/" + config.cameraName() + " connected", true);
  }

  public void updateInputs(CameraIOInputs inputs) {
    simSystem.adjustCamera(cameraSim, config.robotToCamera().get());
    inputs.connected =
        SmartDashboard.getBoolean("Vision/" + config.cameraName() + " connected", true);
    simSystem.update(poseSupplier.get());
    inputs.unreadResults = camera.getAllUnreadResults();
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
