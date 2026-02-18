package frc.robot.commands;

import com.therekrab.autopilot.APTarget;
import com.therekrab.autopilot.Autopilot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drivetrain.AutopilotConstants;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class DriveToPoint implements CommandBuilder {
    private final APTarget target;
    private final Autopilot autopilot;

    public DriveToPoint(APTarget target, Autopilot autopilot) {
        this.target = target;
        this.autopilot = autopilot;
    }

    public DriveToPoint(APTarget target) {
        this(target, AutopilotConstants.kDefaultAutopilot);
    }

    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.drivetrain().driveTo(target, autopilot);
    }
}
