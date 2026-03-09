package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Operator;
import frc.robot.commands.AgitateIntake;
import frc.robot.commands.DumpFuel;
import frc.robot.commands.RetractIntake;
import frc.robot.commands.RunIndex;
import frc.robot.superstructure.Superstructure;

public class OperatorPS5Bindings implements Binder {
    private final CommandPS5Controller controller;

    private final Trigger index;
    private final Trigger eject;
    private final Trigger agitate;
    private final Trigger retract;

    public OperatorPS5Bindings() {
        controller = new CommandPS5Controller(Operator.kOperatorControllerPort);

        index = controller.R1();
        eject = controller.cross();
        agitate = controller.square();
        retract = controller.triangle();
    }

    public void bind(Superstructure superstructure) {
        index.whileTrue(superstructure.build(new RunIndex()));
        eject.whileTrue(superstructure.build(new DumpFuel()));
        agitate.whileTrue(superstructure.build(new AgitateIntake()));
        retract.onTrue(superstructure.build(new RetractIntake()));
    }
}
