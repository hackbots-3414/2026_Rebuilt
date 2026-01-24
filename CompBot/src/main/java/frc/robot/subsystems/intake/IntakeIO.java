package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.OnboardLogger;


public interface IntakeIO {
    public void updateInputs (IntakeIOInputs inputs);

    class IntakeIOInputs {
      public boolean motorConnected = false;
      public Current supplyCurrent = Amps.zero();
      public Current torqueCurrent = Amps.zero();
      public Current statorCurrent = Amps.zero();
      public Voltage voltage = Volts.zero();
      public Temperature temperature = Celsius.zero();

      public IntakeIOInputs() {
        OnboardLogger log = new OnboardLogger("Intake");
        log.registerBoolean("Motor Connected", () -> motorConnected);
        log.registerMeasurment("Supply Current", () -> supplyCurrent, Amps);
        log.registerMeasurment("Torque Current", () -> torqueCurrent, Amps);
        log.registerMeasurment("Stator Current", () -> statorCurrent, Amps);
        log.registerMeasurment("Voltage", () -> voltage, Volts);
        log.registerMeasurment("Temperature", () -> temperature, Celsius);
      }
  }

  public void setCurrent (Current current);
}

