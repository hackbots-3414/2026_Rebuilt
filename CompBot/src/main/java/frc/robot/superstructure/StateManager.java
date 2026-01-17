package frc.robot.superstructure;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.superstructure.Superstructure.Subsystems;

public class StateManager {
    private final Subsystems subsystems;

    public StateManager(Subsystems subsystems) {
        this.subsystems = subsystems;
    }

    public Pose2d robotPose() {
        return subsystems.drivetrain().robotPose();
    }
}
