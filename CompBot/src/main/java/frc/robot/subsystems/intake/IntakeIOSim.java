package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;

public class IntakeIOSim implements IntakeIO {
  private Current current = Amps.zero();
  private Voltage voltageRoller;
  private Voltage voltageDrop;

  public void updateInputs(IntakeIOInputs inputs) {
    inputs.motorRollerConnected = true;
    inputs.torqueCurrent = current;
  }

  public void setCurrent(Current current) {
    this.current = current;
  }

  public void setRollerVoltage(Voltage voltage){
    this.voltageRoller = voltage;
  }

  public void setDropVoltage(Voltage voltage){
    this.voltageDrop = voltage;
  }

}
