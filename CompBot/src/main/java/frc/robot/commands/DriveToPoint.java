package frc.robot.commands;

import com.therekrab.autopilot.APTarget;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class DriveToPoint implements CommandBuilder {
    private final APTarget target;

    public DriveToPoint(APTarget target) {
        this.target = target;
    }

    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.drivetrain().driveTo(target);
    }
}
