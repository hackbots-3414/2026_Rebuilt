package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.Robot;
import frc.robot.commands.CommandBuilder;
import frc.robot.commands.FuelShotSim;
import frc.robot.commands.RunClimb;
import frc.robot.subsystems.climber.ClimberConstants.ClimberPositions;
import frc.robot.superstructure.Superstructure;

public class RobotBindings implements Binder {
    public void bind(Superstructure superstructure) {
        CommandBuilder shoot = (Robot.isReal()) ? new FuelShotSim() : new FuelShotSim();
        superstructure.state.shootReady().whileTrue(
            superstructure.build(shoot).repeatedly()
        );
        RobotModeTriggers.teleop().onTrue(
            superstructure.build(new RunClimb(ClimberPositions.Ready)).onlyIf(superstructure.state.climbed())
        );
    }
}
