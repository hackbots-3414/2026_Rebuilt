package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Revolutions;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.OnboardLogger;

public interface ShooterIO {
  public void updateInputs(ShooterIOInputs inputs);

  class ShooterIOInputs {
    public boolean flywheelMotorConnected = false;
    public Current flywheelSupplyCurrent = Amps.zero();
    public Current flywheelTorqueCurrent = Amps.zero();
    public Current flywheelStatorCurrent = Amps.zero();
    public Voltage flywheelVoltage = Volts.zero();
    public Temperature flywheelTemperature = Celsius.zero();
    public AngularVelocity flywheelVelocity = RotationsPerSecond.zero();

    public boolean hoodMotorConnected = false;
    public Current hoodSupplyCurrent = Amps.zero();
    public Current hoodTorqueCurrent = Amps.zero();
    public Current hoodStatorCurrent = Amps.zero();
    public Voltage hoodVoltage = Volts.zero();
    public Temperature hoodTemperature = Celsius.zero();
    public AngularVelocity hoodVelocity = RotationsPerSecond.zero();
    public Angle hoodAngle = Radians.zero();

    public ShooterIOInputs() {
      OnboardLogger log = new OnboardLogger("Shooter");
      log.registerBoolean("Flywheel Motor Connected", () -> flywheelMotorConnected);
      log.registerMeasurment("Flywheel Supply Current", () -> flywheelSupplyCurrent, Amps);
      log.registerMeasurment("Flywheel Torque Current", () -> flywheelTorqueCurrent, Amps);
      log.registerMeasurment("Flywheel Stator Current", () -> flywheelStatorCurrent, Amps);
      log.registerMeasurment("Flywheel Voltage", () -> flywheelVoltage, Volts);
      log.registerMeasurment("Flywheel Temperature", () -> flywheelTemperature, Celsius);
      log.registerMeasurment("Flywheel Velocity", () -> flywheelVelocity, RotationsPerSecond);

      log.registerBoolean("Hood Motor Connected", () -> hoodMotorConnected);
      log.registerMeasurment("Hood Supply Current", () -> hoodSupplyCurrent, Amps);
      log.registerMeasurment("Hood Torque Current", () -> hoodTorqueCurrent, Amps);
      log.registerMeasurment("Hood Stator Current", () -> hoodStatorCurrent, Amps);
      log.registerMeasurment("Hood Voltage", () -> hoodVoltage, Volts);
      log.registerMeasurment("Hood Temperature", () -> hoodTemperature, Celsius);
      log.registerMeasurment("Hood Velocity", () -> hoodVelocity, RotationsPerSecond);
      log.registerMeasurment("Hood Angle", () -> hoodAngle, Revolutions);
    }
  }

  // void setVoltage(double voltage);
  public void setVelocity(AngularVelocity velocity);
  public void setAngle(Angle angle);
}
