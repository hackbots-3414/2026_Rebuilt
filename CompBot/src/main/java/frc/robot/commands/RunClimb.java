package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;

public class RunClimb implements CommandBuilder {
    
    private final ClimbPosition climberLevel;

    public RunClimb(ClimbPosition climberLevel) {
        this.climberLevel = climberLevel;
    }

    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.climber().go(climberLevel);
    }
}
