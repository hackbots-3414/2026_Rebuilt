package frc.robot.superstructure;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.vision.localization.TimestampedPoseEstimate;

public class StateManager {
    private final Subsystems subsystems;

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

    public void addPoseEstimate(TimestampedPoseEstimate estimate) {
        subsystems.drivetrain().addPoseEstimate(estimate);
    }
}
