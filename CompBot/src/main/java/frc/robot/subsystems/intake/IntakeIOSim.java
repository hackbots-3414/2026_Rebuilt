package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Voltage;

public class IntakeIOSim implements IntakeIO {
  private Voltage voltage = Volts.zero();
  private Angle position = Rotations.zero();

  public void updateInputs(IntakeIOInputs inputs) {
    inputs.intakeVoltage = voltage;
    inputs.deployPosition = position;
  }

  public void setIntakeVoltage(Voltage voltage) {
    this.voltage = voltage;
  }

  public void setDeployPosition(Angle angle) {
    position = angle;
  }
}
