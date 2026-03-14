package frc.robot.binding;

import com.pathplanner.lib.auto.NamedCommands;

import frc.robot.commands.AimPrep;
import frc.robot.commands.RunIndex;
import frc.robot.commands.RunIntake;
import frc.robot.subsystems.intake.Intake;
import frc.robot.superstructure.Superstructure;

public class NamedCommandBindings implements Binder {
    public void bind(Superstructure superstructure) {
        NamedCommands.registerCommand("Aim", superstructure.build(new AimPrep()));
        NamedCommands.registerCommand("Deploy Intake", superstructure.build(new RunIndex()));
        NamedCommands.registerCommand("Intake", superstructure.build(new RunIntake()));
    }    
}
