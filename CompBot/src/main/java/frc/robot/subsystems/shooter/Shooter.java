
package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;

public class Shooter extends SubsystemBase {
  private final ShooterIO io;
  private final ShooterIOInputs inputs = new ShooterIOInputs();

  public Shooter(ShooterIO io) {
    this.io = io;
  }
  
  @Override
  public void periodic() {
  }

}
