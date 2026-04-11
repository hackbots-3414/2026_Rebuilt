package frc.robot.binding;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.events.EventTrigger;

import frc.robot.commands.AimPrep;
import frc.robot.commands.RetractIntake;
import frc.robot.commands.RunIntake;
import frc.robot.superstructure.Superstructure;

public class NamedCommandBindings implements Binder {
    public void bind(Superstructure superstructure) {
        NamedCommands.registerCommand("Aim", superstructure.build(new AimPrep()));
        NamedCommands.registerCommand("Intake", superstructure.build(new RunIntake()));
        NamedCommands.registerCommand("Retract", superstructure.build(new RetractIntake())) ;
        
        new EventTrigger("Aim").whileTrue(superstructure.build(new AimPrep()));
        new EventTrigger("Intake").whileTrue(superstructure.build(new RunIntake()));
    }    
}
