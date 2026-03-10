package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants.DeployPosition;

public class AgitateIntake implements CommandBuilder {
    public Command build(Subsystems subsystems, StateManager state) {
        return Commands.repeatingSequence(
            subsystems.intake().intakeAt(DeployPosition.Agitate).withTimeout(0.5),
            Commands.waitSeconds(0.15),
            subsystems.intake().intakeAt(DeployPosition.Deployed).withTimeout(0.5),
            Commands.waitSeconds(0.15));
    }
}
