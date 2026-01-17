package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Superstructure.Subsystems;

public interface CommandBuilder {
    public Command build(Subsystems subsystems);
}
