package frc.robot.util;

import static edu.wpi.first.units.Units.Meters;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
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
      return new Pose3d(
          FieldConstants.kFieldLength.minus(pose.getMeasureX()),
          FieldConstants.kFieldWidth.minus(pose.getMeasureY()),
          pose.getMeasureZ(),
          pose.getRotation().rotateBy(new Rotation3d(Rotation2d.kPi)));
    } else {
      return pose;
    }
  }

  private static Pose2d allianceRelativeFlip(Pose2d pose) {
    return allianceRelativeFlip(new Pose3d(pose)).toPose2d();
  }

  /** Returns the position of the hub corresponding to the currently selected alliance */
  public static Pose3d hub() {
    return allianceRelativeFlip(FieldConstants.kBlueHub);
  }

  public static boolean inAllianceZone(Pose2d robot) {
    Pose2d local = allianceRelativeFlip(robot);
    return local.getX() <= FieldConstants.kAllianzeZoneLength.in(Meters);
  }

  public static Pose3d feedTarget() {
    return allianceRelativeFlip(FieldConstants.kFeedTarget);
  }
}
