package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.aiming.AimParams;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class DrivetrainTrack implements CommandBuilder {
  public Command build(Subsystems subsystems, StateManager state) {
    AimParams params = new AimParams();
    AimStrategy aim = new PhysicsAim(0.1);
    return subsystems.drivetrain().track(() -> aim.update(state, params));
  }
}
