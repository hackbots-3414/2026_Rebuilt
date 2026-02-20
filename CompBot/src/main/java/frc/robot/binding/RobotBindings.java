package frc.robot.binding;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.commands.CommandBuilder;
import frc.robot.commands.ShootWhenReady;
import frc.robot.commands.RunClimb;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.superstructure.Superstructure;

public class RobotBindings implements Binder {
    public void bind(Superstructure superstructure) {
        CommandBuilder shoot = new ShootWhenReady();
        superstructure.state.shootReady().debounce(0.25, DebounceType.kFalling).whileTrue(
            superstructure.build(shoot).repeatedly()
        );
        RobotModeTriggers.teleop().onTrue(
            superstructure.build(new RunClimb(ClimbPosition.Ready)).onlyIf(superstructure.state.climbing())
        );
    }
}
