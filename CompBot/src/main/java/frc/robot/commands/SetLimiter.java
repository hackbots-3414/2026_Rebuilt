package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;
import frc.robot.util.ChassisSpeedRateLimiter;

public class SetLimiter implements CommandBuilder { 
  private ChassisSpeedRateLimiter limit;
  public SetLimiter(ChassisSpeedRateLimiter limit) {
    this.limit = limit;
  }
  public Command build(Subsystems subsystems, StateManager state) {
    // return subsystems.indexer().indexWithVariation();
    return Commands.runOnce(() -> subsystems.drivetrain().setRateLimiter(limit));
  }
}
