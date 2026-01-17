package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.superstructure.Superstructure.Subsystems;

public class VisionTest implements CommandBuilder {
    public Command build(Subsystems subsystems) {
        return subsystems.drivetrain().rotate();
    }
}
