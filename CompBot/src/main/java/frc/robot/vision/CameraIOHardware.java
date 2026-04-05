package frc.robot.vision;

import org.photonvision.PhotonCamera;

public class CameraIOHardware implements CameraIO {
  private final PhotonCamera camera;

  public CameraIOHardware(CameraConfig config) {
    camera = new PhotonCamera(config.cameraName());
  }

  public void updateInputs(CameraIOInputs inputs) {
    inputs.connected = camera.isConnected();
    inputs.unreadResults = camera.getAllUnreadResults();
  }
}
