package frc.robot.vision;

import org.photonvision.PhotonCamera;

public class CameraIOHardware implements CameraIO {
  private final PhotonCamera camera;
  private final String name;

  public CameraIOHardware(String name) {
    camera = new PhotonCamera(name);
    this.name = name;
  }

  public void updateInputs(CameraIOInputs inputs) {
    inputs.connected = camera.isConnected();
    inputs.unreadResults = camera.getAllUnreadResults();
  }

  public String getName() {
    return name;
  }
}