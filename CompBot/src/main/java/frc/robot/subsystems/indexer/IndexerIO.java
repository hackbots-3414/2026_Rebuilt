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
    public boolean feedMotorConnected = false;
    public Current feedSupplyCurrent = Amps.zero();
    public Current feedTorqueCurrent = Amps.zero();
    public Current feedStatorCurrent = Amps.zero();
    public Voltage feedVoltage = Volts.zero();
    public Temperature feedTemperature = Celsius.zero();
    public AngularVelocity feedVelocity = RotationsPerSecond.zero();

    public boolean spindexerMotorConnected = false;
    public Current spindexerSupplyCurrent = Amps.zero();
    public Current spindexerTorqueCurrent = Amps.zero();
    public Current spindexerStatorCurrent = Amps.zero();
    public Voltage spindexerVoltage = Volts.zero();
    public Temperature spindexerTemperature = Celsius.zero();
    public AngularVelocity spindexerVelocity = RotationsPerSecond.zero();

    public IndexerIOInputs() {
      OnboardLogger log = new OnboardLogger("Indexer");
      log.registerBoolean("Feeder Motor Connected", () -> feedMotorConnected);
      log.registerMeasurment("Feeder Supply Current", () -> feedSupplyCurrent, Amps);
      log.registerMeasurment("Feeder Torque Current", () -> feedTorqueCurrent, Amps);
      log.registerMeasurment("Feeder Stator Current", () -> feedStatorCurrent, Amps);
      log.registerMeasurment("Feeder Voltage", () -> feedVoltage, Volts);
      log.registerMeasurment("Feeder Temperature", () -> feedTemperature, Celsius);
      log.registerMeasurment("Feeder Velocity", () -> feedVelocity, RotationsPerSecond);

      log.registerBoolean("Spindexer Motor Connected", () -> spindexerMotorConnected);
      log.registerMeasurment("Spindexer Supply Current", () -> spindexerSupplyCurrent, Amps);
      log.registerMeasurment("Spindexer Torque Current", () -> spindexerTorqueCurrent, Amps);
      log.registerMeasurment("Spindexer Stator Current", () -> spindexerStatorCurrent, Amps);
      log.registerMeasurment("Spindexer Voltage", () -> spindexerVoltage, Volts);
      log.registerMeasurment("Spindexer Temperature", () -> spindexerTemperature, Celsius);
      log.registerMeasurment("Spindexer Velocity", () -> spindexerVelocity, RotationsPerSecond);
    }
  }

  public void setFeedVoltage(Voltage voltage);

  public void setSpindexerVoltage(Voltage voltage);
}
