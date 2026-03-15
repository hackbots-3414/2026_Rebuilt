package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Robot;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class ShootWhenReady implements CommandBuilder {
    public Command build(Subsystems subsystems, StateManager state) {
        Command composition = Commands.sequence(
            Commands.waitUntil(state.shootReady),
            subsystems.indexer().index());
        if (Robot.isReal()) {
            return composition;
        }
        return composition.alongWith(new FuelShotSim().build(subsystems, state).repeatedly());
    }
}
