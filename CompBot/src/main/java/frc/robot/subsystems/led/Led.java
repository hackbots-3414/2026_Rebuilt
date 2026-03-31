package frc.robot.subsystems.led;

import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.subsystems.led.ledStates.*;
import frc.robot.superstructure.StateManager;

public class Led extends SubsystemBase {
  private LedState appliedState = new Default();
  private LedIO io;

  public Led(LedIO io) {
    super();
    this.io = io;
    if (DriverStation.isTest() || Robot.isSimulation()) {
      for (LedState state : hierarchy) {
        SmartDashboard.putBoolean("Led/Enable " + state.getClass().getSimpleName(), false);
      }
    }
  }

  private List<LedState> hierarchy = List.of(
      new BadController(),
      new TestRslEnabled(),
      new TestRsl(),
      new EndGameAlert(),
      new EndGameWarning(),
      new OurShiftIsEnding(),
      new TheirShiftIsEnding(),
      new HubActive(),
      new Default());

  public void update(StateManager stateManager) {
    for (LedState state : hierarchy) {
      boolean check = state.check(stateManager);
      if (Robot.isSimulation()) {
        String name = state.getClass().getSimpleName();
        check = SmartDashboard.getBoolean("Led/Enable " + name, false);
        SmartDashboard.putBoolean("Led/Enable " + name, check);
      }
      if (!check) {
        continue;
      }
      if (appliedState.equals(state)) {
        break;

      }
      io.applyAnimation(state.apply(io));
      appliedState = state;
      return;
    }
  }
}
