package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;

public class Climber extends SubsystemBase {
  private final ClimberIO io;
  private final ClimberIOInputs inputs =  new ClimberIOInputs();

  public Climber(ClimberIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    
  }

}