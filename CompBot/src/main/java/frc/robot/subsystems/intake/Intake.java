
package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Robot;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;

public class Intake extends SubsystemBase {
  private final IntakeIO io;
  private final IntakeIOInputs inputs;

  public Intake(IntakeIO io) {
    super();
    this.io = io;
    inputs = new IntakeIOInputs();
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  private void setVoltage(Current voltage){
    io.setCurrent(voltage);
  }

  public Command intakeFuel() {
    return runOnce(() -> io.setVoltage(IntakeConstants.kIntakeVoltage));    
  }

  public Command reverse() {
    return runOnce(() -> io.setVoltage(IntakeConstants.kEjectVoltage)); // process of ejecting
  }

  public Trigger detectJam(boolean hasFuel ){
    return new Trigger(() ->  
    (inputs.supplyCurrent.in(Amps) > IntakeConstants.kStatorCurrentLimit) && 
    (inputs.hasFuel));    
  }
}
