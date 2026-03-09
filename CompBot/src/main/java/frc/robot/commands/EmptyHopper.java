package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class EmptyHopper implements CommandBuilder {
  public Command build(Subsystems subsystems, StateManager state) {
    return Commands.parallel(
      new AimPrep().build(subsystems, state),
      new ShootWhenReady().build(subsystems, state).until(subsystems.shooter().seenBall(2))
    );
  }
}
