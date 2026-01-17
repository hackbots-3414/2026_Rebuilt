package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.CommandBuilder;
import frc.robot.Superstructure.Subsystems;

public class DriveForward implements CommandBuilder {
    public Command build(Subsystems subsystems) {
        return subsystems.drivetrain().driveForwards();
    }
}
