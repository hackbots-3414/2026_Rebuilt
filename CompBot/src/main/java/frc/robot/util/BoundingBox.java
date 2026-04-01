package frc.robot.util;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * Simple Axis-Aligned Bounding Box (AABB), useful for simple rectangles in math.
 * 
 * @param lower The "lower" corner of the bounding box, with the smaller X and Y components.
 * @param upper The "upper" corner of the bounding box, with the larger X and Y compontents.
 * 
 */
public record BoundingBox(
  Pose2d lower,
  Pose2d upper
) {
  
  /**
   * Returns whether the given pose in included in the AABB.
   * @param pose The pose to check
   * @return Whether the given pose is included in the AABB
   */
  public boolean contains(Pose2d pose) {
    double x = pose.getX();
    double y = pose.getY();
    return x >= lower.getX() && x <= upper.getX() && y >= lower.getY() && y <= upper.getY();
  }
}
