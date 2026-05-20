package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class AimPrep implements CommandBuilder {
  public Command build(Subsystems subsystems, StateManager state) {
    return Commands.parallel(
      //subsystems.turret().track(state),
      subsystems.shooter().shoot(state::aimParams));
  }
}
