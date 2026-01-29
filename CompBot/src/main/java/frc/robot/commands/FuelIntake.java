package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class FuelIntake implements CommandBuilder {

    public FuelIntake() {}

    @Override
    public Command build(Subsystems subsystems, StateManager state) {
        return Commands.sequence(
            subsystems.intake().dropIntake(),
            subsystems.intake().intakeFuel()
        );
    }
    
}
