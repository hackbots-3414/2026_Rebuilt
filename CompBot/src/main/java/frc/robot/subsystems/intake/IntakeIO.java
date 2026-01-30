package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Centimeters;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.OnboardLogger;

public interface IntakeIO {
  public void updateInputs(IntakeIOInputs inputs);

  class IntakeIOInputs {
    public boolean intakeMotorConnected = false;
    public Current intakeSupplyCurrent = Amps.zero();
    public Current intakeTorqueCurrent = Amps.zero();
    public Current intakeStatorCurrent = Amps.zero();
    public AngularVelocity intakeVelocity = RadiansPerSecond.of(0);
    public Voltage intakeVoltage = Volts.zero();
    public Temperature intakeTemperature = Celsius.zero();

    public boolean deployMotorConnected = false;
    public Current deploySupplyCurrent = Amps.zero();
    public Current deployTorqueCurrent = Amps.zero();
    public Current deployStatorCurrent = Amps.zero();
    public AngularVelocity deployVelocity = RadiansPerSecond.of(0);
    public Voltage deployVoltage = Volts.zero();
    public Temperature deployTemperature = Celsius.zero();
    public Angle deployPosition = Rotations.zero();

    public boolean canrangeConnected = false;
    public Distance canrangeDistance = Meters.zero();
    public boolean canrangeDetected = false;

    public IntakeIOInputs() {
      OnboardLogger log = new OnboardLogger("Intake");
      log.registerBoolean("Intake Motor/Connected", () -> intakeMotorConnected);
      log.registerMeasurment("Intake Motor/Supply Current", () -> intakeSupplyCurrent, Amps);
      log.registerMeasurment("Intake Motor/Torque Current", () -> intakeTorqueCurrent, Amps);
      log.registerMeasurment("Intake Motor/Stator Current", () -> intakeStatorCurrent, Amps);
      log.registerMeasurment("Intake Motor/Voltage", () -> intakeVoltage, Volts);
      log.registerMeasurment("Intake Motor/Temperature", () -> intakeTemperature, Celsius);

      log.registerBoolean("Deploy Motor/Connected", () -> deployMotorConnected);
      log.registerMeasurment("Deploy Motor/Supply Current", () -> deploySupplyCurrent, Amps);
      log.registerMeasurment("Deploy Motor/Torque Current", () -> deployTorqueCurrent, Amps);
      log.registerMeasurment("Deploy Motor/Stator Current", () -> deployStatorCurrent, Amps);
      log.registerMeasurment("Deploy Motor/Voltage", () -> deployVoltage, Volts);
      log.registerMeasurment("Deploy Motor/Temperature", () -> deployTemperature, Celsius);
      log.registerMeasurment("Deploy Motor/Position", () -> deployPosition, Degrees);

      log.registerBoolean("CANrange Connected", () -> canrangeConnected);
      log.registerMeasurment("CANrange Distance", () -> canrangeDistance, Centimeters);
      log.registerBoolean("CANrange Detected", () -> canrangeDetected);
    }
  }

  public void setIntakeCurrent(Current current);

  public void setDeployPosition(Angle position);
}

