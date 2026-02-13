package frc.robot.vision;

import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.units.measure.Angle;

/**
 * A class representing the physical and software configuration of the camera.
 * 
 * @param cameraName The name of the camera.
 * @param resolutionHeight The vertical resolution of the camera, in pixels
 * @param resolutionWidth The horizontal resolution of the camera, in pixels
 * @param fov The diagonal field of view of the camera
 * @param fovHorizontal The horizontal field of view of the camera
 * @param robotToCamera A supplier which returns the robot-relative position of the camera, in NWU
 *        coordinates Please also note that pitch is negative.
 * @param trust The trust parameters for this camera
 */
public record CameraConfig(
    String cameraName,
    int resolutionHeight,
    int resolutionWidth,
    Angle fov,
    Angle fovHorizontal,
    Supplier<Transform3d> robotToCamera,
    CameraTrustConfig trust) {

  /** Returns a deep copy of this camera config */
  public CameraConfig copy() {
    return new CameraConfig(
        cameraName,
        resolutionHeight,
        resolutionWidth,
        fov,
        fovHorizontal,
        robotToCamera,
        trust.copy());
  }

  /** Returns a modified deep copy of this camera config */
  public CameraConfig cameraCopy(String cameraName, Supplier<Transform3d> robotToCamera) {
    return new CameraConfig(
        cameraName,
        resolutionHeight,
        resolutionWidth,
        fov,
        fovHorizontal,
        robotToCamera,
        trust);
  }
}
