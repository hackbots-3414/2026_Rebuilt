package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import edu.wpi.first.units.measure.Current;

public class IntakeIOSim implements IntakeIO {
  private Current current = Amps.zero();

  public void updateInputs(IntakeIOInputs inputs) {
    inputs.motorConnected = true;
    inputs.torqueCurrent = current;
  }

  public void setCurrent(Current current) {
    this.current = current;
  }

}
