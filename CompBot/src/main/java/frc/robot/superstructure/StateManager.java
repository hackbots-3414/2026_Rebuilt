package frc.robot.superstructure;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.util.FieldUtils;

/**
 * A class representing the robot-wide state variables.
 */
public class StateManager {
  private final Subsystems subsystems;
  public final AimStrategy aim = new PhysicsAim(2.0);

  public StateManager(Subsystems subsystems) {
    this.subsystems = subsystems;
  }

  /**
   * Returns the robot's position on the field.
   */
  public Pose2d robotPose() {
    return subsystems.drivetrain().robotPose();
  }

  /**
   * Returns the robot's field-relative velocity
   */
  public Transform2d robotVelocity() {
    return subsystems.drivetrain().robotVelocity();
  }

  public Pose3d aimTarget() {
    // For now, just assume that we're targeting the nearest hub.
    // TODO: add feeding logic as well.
    return FieldUtils.hub();
  }

  public Trigger shootReady() {
    return subsystems.turret().tracked();
  }

  public AimParams aimParams() {
    return aim.params;
  }

  public void periodic() {
    aim.update(this); // This ensures we only cache the parameter object and then cache them.
  }
}
