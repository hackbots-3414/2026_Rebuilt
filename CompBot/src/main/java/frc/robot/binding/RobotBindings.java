package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.Autogen;
import frc.robot.commands.FuelShotSim;
import frc.robot.superstructure.Superstructure;

public class RobotBindings implements Binder {
  public void bind(Superstructure superstructure) {
    superstructure.state.shootReady().whileTrue(superstructure.build(new FuelShotSim()).repeatedly());

    Autogen.registerCommand("A", Commands.print("A ran!"));
    Autogen.registerCommand("B", Commands.print("B ran!"));
    Autogen.registerCommand("C", Commands.print("C ran!"));
  }
}
