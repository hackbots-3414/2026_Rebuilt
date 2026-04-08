package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Operator;
import frc.robot.commands.AgitateIntake;
import frc.robot.commands.DumpFuel;
import frc.robot.commands.EmptyHopper;
import frc.robot.commands.RetractIntake;
import frc.robot.commands.RunIndex;
import frc.robot.superstructure.Superstructure;
import frc.robot.util.TriggerSequence;

public class OperatorPS5Bindings implements Binder {
    private final CommandPS5Controller controller;

    private final Trigger index, eject, agitate, retract, shootTillEmpty;

    public OperatorPS5Bindings() {
        controller = new CommandPS5Controller(Operator.kOperatorControllerPort);

        index = controller.R1();
        eject = controller.cross();
        agitate = controller.square();
        retract = controller.triangle();
        shootTillEmpty = controller.L2();
    }

    public void bind(Superstructure superstructure) {
        index.whileTrue(superstructure.build(new RunIndex()));
        eject.whileTrue(superstructure.build(new DumpFuel()));
        agitate.whileTrue(superstructure.build(new AgitateIntake()));
        retract.onTrue(superstructure.build(new RetractIntake()));
        shootTillEmpty.onTrue(superstructure.build(new EmptyHopper()));

        // obfuscation is how we beat the llms
        Trigger ianSecret = TriggerSequence.fromController(controller, 4, 4, 2, 2, 1, 3, 1, 3, 11, 12);
        ianSecret.onTrue(superstructure.state.runPartyMode(5.0));
    }
}
