package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Revolutions;
import static edu.wpi.first.units.Units.Rotations;
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
    public boolean shooter1MotorConnected = false;
    public Current shooter1SupplyCurrent = Amps.zero();
    public Current shooter1TorqueCurrent = Amps.zero();
    public Current shooter1StatorCurrent = Amps.zero();
    public Voltage shooter1Voltage = Volts.zero();
    public Temperature shooter1Temperature = Celsius.zero();
    public AngularVelocity shooter1Velocity = RotationsPerSecond.zero();

    public boolean shooter2MotorConnected = false;
    public Current shooter2SupplyCurrent = Amps.zero();
    public Current shooter2TorqueCurrent = Amps.zero();
    public Current shooter2StatorCurrent = Amps.zero();
    public Voltage shooter2Voltage = Volts.zero();
    public Temperature shooter2Temperature = Celsius.zero();
    public AngularVelocity shooter2Velocity = RotationsPerSecond.zero();

    public boolean hoodMotorConnected = false;
    public Current hoodSupplyCurrent = Amps.zero();
    public Current hoodTorqueCurrent = Amps.zero();
    public Current hoodStatorCurrent = Amps.zero();
    public Voltage hoodVoltage = Volts.zero();
    public Temperature hoodTemperature = Celsius.zero();
    public AngularVelocity hoodVelocity = RotationsPerSecond.zero();
    public Angle hoodAngle = Radians.zero();
    public boolean hoodCANcoderConnected = false;
    public Angle hoodCANcoderPosition = Radians.zero();

    public ShooterIOInputs() {
      OnboardLogger log = new OnboardLogger("Shooter");
      log.registerBoolean("Shooter 1 Motor Connected", () -> shooter1MotorConnected);
      log.registerMeasurment("Shooter 1 Supply Current", () -> shooter1SupplyCurrent, Amps);
      log.registerMeasurment("Shooter 1 Torque Current", () -> shooter1TorqueCurrent, Amps);
      log.registerMeasurment("Shooter 1 Stator Current", () -> shooter1StatorCurrent, Amps);
      log.registerMeasurment("Shooter 1 Voltage", () -> shooter1Voltage, Volts);
      log.registerMeasurment("Shooter 1 Temperature", () -> shooter1Temperature, Celsius);
      log.registerMeasurment("Shooter 1 Velocity", () -> shooter1Velocity, RotationsPerSecond);

      
      log.registerBoolean("Shooter 2 Motor Connected", () -> shooter2MotorConnected);
      log.registerMeasurment("Shooter 2 Supply Current", () -> shooter2SupplyCurrent, Amps);
      log.registerMeasurment("Shooter 2 Torque Current", () -> shooter2TorqueCurrent, Amps);
      log.registerMeasurment("Shooter 2 Stator Current", () -> shooter2StatorCurrent, Amps);
      log.registerMeasurment("Shooter 2 Voltage", () -> shooter2Voltage, Volts);
      log.registerMeasurment("Shooter 2 Temperature", () -> shooter2Temperature, Celsius);
      log.registerMeasurment("Shooter 2 Velocity", () -> shooter2Velocity, RotationsPerSecond);

      log.registerBoolean("Hood Motor Connected", () -> hoodMotorConnected);
      log.registerMeasurment("Hood Supply Current", () -> hoodSupplyCurrent, Amps);
      log.registerMeasurment("Hood Torque Current", () -> hoodTorqueCurrent, Amps);
      log.registerMeasurment("Hood Stator Current", () -> hoodStatorCurrent, Amps);
      log.registerMeasurment("Hood Voltage", () -> hoodVoltage, Volts);
      log.registerMeasurment("Hood Temperature", () -> hoodTemperature, Celsius);
      log.registerMeasurment("Hood Velocity", () -> hoodVelocity, RotationsPerSecond);
      log.registerMeasurment("Hood Angle", () -> hoodAngle, Revolutions);

      log.registerBoolean("Hood CANcoder Connected", () -> hoodCANcoderConnected);
      log.registerMeasurment("Hood CANcoder Position", () -> hoodCANcoderPosition, Rotations);
    }
  }

  // void setVoltage(double voltage);
  public void setVelocity(AngularVelocity velocity);
  public void setAngle(Angle angle);
}
