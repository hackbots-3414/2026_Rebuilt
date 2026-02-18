package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class FuelShot implements CommandBuilder {
    public Command build(Subsystems subsystems, StateManager state) {
        CommandBuilder aimPrep = new AimPrep();
        return Commands.parallel(
            aimPrep.build(subsystems, state),
            subsystems.indexer().index()
        );
    }
}
