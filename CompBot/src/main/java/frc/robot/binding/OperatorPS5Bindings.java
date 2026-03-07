package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Operator;
import frc.robot.commands.RunIndex;
import frc.robot.superstructure.Superstructure;

public class OperatorPS5Bindings implements Binder {
    private final CommandPS5Controller controller;

    private final Trigger index;

    public OperatorPS5Bindings() {
        controller = new CommandPS5Controller(Operator.kOperatorControllerPort);

        index = controller.button(1);
    }

    public void bind(Superstructure superstructure) {
        index.onTrue(superstructure.build(new RunIndex()));
    }
}
