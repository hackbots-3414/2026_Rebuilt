package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class ResetOdometry implements CommandBuilder {
    private final Pose2d pose;

    public ResetOdometry(Pose2d pose) {
        this.pose = pose;
    }

    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.drivetrain().resetOdometry(pose);
    }
}
