// This is copied from the wpilib repo

package frc.robot;

import org.junit.jupiter.api.BeforeEach;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class CommandBasedTest {
  @BeforeEach
  void commandSetup() {
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().enable();
    CommandScheduler.getInstance().getActiveButtonLoop().clear();
    CommandScheduler.getInstance().clearComposedCommands();
    CommandScheduler.getInstance().unregisterAllSubsystems();

    setDSEnabled(true);
  }

  public void setDSEnabled(boolean enabled) {
    DriverStationSim.setDsAttached(true);

    DriverStationSim.setEnabled(enabled);
    DriverStationSim.notifyNewData();
    while (DriverStation.isEnabled() != enabled) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      }
    }
  }
}
