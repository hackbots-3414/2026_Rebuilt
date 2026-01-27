package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.OnboardLogger;

public interface IndexerIO {
  public void updateInputs(IndexerIOInputs inputs);

  class IndexerIOInputs {
    public boolean motorConnected = false;
    public Current supplyCurrent = Amps.zero();
    public Current torqueCurrent = Amps.zero();
    public Current statorCurrent = Amps.zero();
    public Voltage voltage = Volts.zero();
    public Temperature temperature = Celsius.zero();
    public AngularVelocity velocity = RotationsPerSecond.zero();

    public IndexerIOInputs() {
      OnboardLogger log = new OnboardLogger("Indexer");
      log.registerBoolean("Motor Connected", () -> motorConnected);
      log.registerMeasurment("Supply Current", () -> supplyCurrent, Amps);
      log.registerMeasurment("Torque Current", () -> torqueCurrent, Amps);
      log.registerMeasurment("Stator Current", () -> statorCurrent, Amps);
      log.registerMeasurment("Voltage", () -> voltage, Volts);
      log.registerMeasurment("Temperature", () -> temperature, Celsius);
      log.registerMeasurment("Velocity", () -> velocity, RotationsPerSecond);
    }
  }

  public void setVoltage(Voltage voltage);

  public void stop();
}
