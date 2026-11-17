package frc.robot.binding;

import frc.robot.commands.AgitateIntake;
import frc.robot.commands.CommandBuilder;
import frc.robot.commands.RunIndex;
import frc.robot.superstructure.Superstructure;

public class RobotBindings implements Binder {
    public void bind(Superstructure superstructure) {
        CommandBuilder shoot = new RunIndex();
        superstructure.state.shootReady.whileTrue(superstructure.build(shoot).repeatedly());
        superstructure.state.shootReady.and(superstructure.state.intaking().negate()).whileTrue(superstructure.build(new AgitateIntake()));
    }
}
