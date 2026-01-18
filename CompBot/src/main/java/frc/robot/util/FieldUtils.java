package frc.robot.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.FieldConstants;

/**
 * A class with several static methods to help field-related tasks, such as getting the pose of
 * something on the field
 */
public class FieldUtils {
  private static Pose3d allianceRelativeFlip(Pose3d pose) {
    if (DriverStation.getAlliance().orElse(Alliance.Blue).equals(Alliance.Red)) {
      return null;
    } else {
      return pose;
    }
  }

  private static Pose2d allianceRelativeFlip(Pose2d pose) {
    if (DriverStation.getAlliance().orElse(Alliance.Blue).equals(Alliance.Red)) {
      return null;
    } else {
      return pose;
    }
  }

  /** Returns the position of the hub corresponding to the currently selected alliance */
  public static Pose3d hub() {
    return FieldConstants.kBlueHub;
  }
}
