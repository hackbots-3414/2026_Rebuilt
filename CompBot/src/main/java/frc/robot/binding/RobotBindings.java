package frc.robot.binding;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Robot;
import frc.robot.commands.FuelShotSim;
import frc.robot.superstructure.Superstructure;

public class RobotBindings implements Binder {
  public void bind(Superstructure superstructure) {
    Command autoShoot;
    if (Robot.isSimulation()) {
      autoShoot = superstructure.build(new FuelShotSim()).repeatedly();
    } else {
      // When we merge the shooter branch in, it will come with an implementation here-for now, the indexer branch doesn't need to implement shooting.
      autoShoot = Commands.print("Shoot not yet implemented");
    }
    superstructure.state.shootReady().whileTrue(autoShoot);
  }
}
