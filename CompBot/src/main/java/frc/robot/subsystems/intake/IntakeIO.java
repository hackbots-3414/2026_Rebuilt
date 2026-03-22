package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
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

    public boolean deployCANcoderConnected = false;
    public Angle deployCANcoderPosition = Rotations.zero();
    public AngularVelocity deployCANcoderVelocity = RadiansPerSecond.of(0);

    public IntakeIOInputs() {
      OnboardLogger log = new OnboardLogger("Intake");
      log.registerBoolean("Intake Motor/Connected", () -> intakeMotorConnected);
      log.registerMeasurement("Intake Motor/Supply Current", () -> intakeSupplyCurrent, Amps);
      log.registerMeasurement("Intake Motor/Torque Current", () -> intakeTorqueCurrent, Amps);
      log.registerMeasurement("Intake Motor/Stator Current", () -> intakeStatorCurrent, Amps);
      log.registerMeasurement("Intake Motor/Voltage", () -> intakeVoltage, Volts);
      log.registerMeasurement("Intake Motor/Temperature", () -> intakeTemperature, Celsius);
      log.registerMeasurement("Intake Motor/Velocity", () -> intakeVelocity, RotationsPerSecond);

      log.registerBoolean("Deploy Motor/Connected", () -> deployMotorConnected);
      log.registerMeasurement("Deploy Motor/Supply Current", () -> deploySupplyCurrent, Amps);
      log.registerMeasurement("Deploy Motor/Torque Current", () -> deployTorqueCurrent, Amps);
      log.registerMeasurement("Deploy Motor/Stator Current", () -> deployStatorCurrent, Amps);
      log.registerMeasurement("Deploy Motor/Voltage", () -> deployVoltage, Volts);
      log.registerMeasurement("Deploy Motor/Temperature", () -> deployTemperature, Celsius);
      log.registerMeasurement("Deploy Motor/Position", () -> deployPosition, Rotations);

      log.registerBoolean("CANcoder/Connected", () -> deployCANcoderConnected);
      log.registerMeasurement("CANcoder/Position", () -> deployCANcoderPosition, Rotations);
      log.registerMeasurement("CANcoder/Velocity", () -> deployCANcoderVelocity, RotationsPerSecond);
    }
  }

  public void setIntakeVoltage(Voltage voltage);

  public void setDeployPosition(Angle position);
}

