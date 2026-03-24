package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.AimPrep;
import frc.robot.commands.ResetForwards;
import frc.robot.commands.RetractIntake;
import frc.robot.commands.RunIndex;
import frc.robot.commands.RunIntake;
import frc.robot.subsystems.drivetrain.Drivetrain.TeleopDriveMode;
import frc.robot.superstructure.Superstructure;

public class KeyboardBindings implements Binder {
    private final CommandGenericHID controller;

    private final Trigger shoot, intake, robotCentricDrive, resetForwards, index, retractIntake;

    private final DoubleSupplier vx, vy, vrot;

    public KeyboardBindings() {
        controller = new CommandGenericHID(0);

        shoot = controller.button(1);
        intake = controller.button(2);
        robotCentricDrive = controller.button(3);
        resetForwards = controller.button(4);
        index = controller.button(5);
        retractIntake = controller.button(6);

        vx = () -> controller.getRawAxis(0);
        vy = () -> controller.getRawAxis(1);
        vrot = () -> controller.getRawAxis(2);
    }

    public void bind(Superstructure superstructure) {
        superstructure.bindDrive(vx, vy, vrot, () -> robotCentricDrive.getAsBoolean() ? TeleopDriveMode.RobotRelative : TeleopDriveMode.FieldRelativeSpin);

        shoot.toggleOnTrue(superstructure.build(new AimPrep()));
        intake.whileTrue(superstructure.build(new RunIntake()));
        resetForwards.onTrue(superstructure.build(new ResetForwards()));
        index.whileTrue(superstructure.build(new RunIndex()));
        retractIntake.onTrue(superstructure.build(new RetractIntake()));

    }
    
}
