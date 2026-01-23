package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;


public interface IntakeIO {
    public void updateInputs (IntakeIOInputs inputs);

    class IntakeIOInputs {
      public boolean motorConnected = true;
      public Current supplyCurrent = Amps.zero();
      public Current torqueCurrent = Amps.zero();
      public Current statorCurrent = Amps.zero();
      public Voltage voltage = Volts.zero();
      public Temperature temperature = Celsius.zero();
  }

  public void setCurrent (Current current);
}

