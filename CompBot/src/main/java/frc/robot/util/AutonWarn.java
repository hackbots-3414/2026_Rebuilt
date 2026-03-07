package frc.robot.util;

import java.util.Optional;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import frc.autogen.Autogen;

public class AutonWarn {
  public static final double TRANSLATION_ERROR = 0.5; // meters
  public static final double ROTATION_ERROR = 0.1; // rotations

  public static boolean checkPose(String autoName, Pose2d robotPose) {
    Optional<Pose2d> start = Autogen.getStartingPose(autoName);
    if (start.isEmpty()) {
      return false;
    }
    Transform2d offset = FieldUtils.allianceRelativeFlip(robotPose).minus(start.get());
    double xyError = offset.getTranslation().getNorm();
    double rotError = Math.abs(offset.getRotation().getRotations());
    return (xyError <= TRANSLATION_ERROR) && (rotError <= ROTATION_ERROR);
  }
}
