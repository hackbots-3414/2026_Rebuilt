package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants.DeployPosition;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class RunIntake implements CommandBuilder {
    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.intake().intakeAt(DeployPosition.Deployed);
    }
}
