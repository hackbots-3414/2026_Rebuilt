package frc.robot.subsystems.climber;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.OnboardLogger;
import static edu.wpi.first.units.Units.Volts;


import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;


public interface ClimberIO {
  void updateInputs(ClimberIOInputs inputs);

  class ClimberIOInputs {
    public boolean motorConnected = false;
    public Current supplyCurrent = Amps.zero();
    public Current torqueCurrent = Amps.zero();
    public Current statorCurrent = Amps.zero();
    public Voltage voltage = Volts.zero();
    public AngularVelocity velocity = RotationsPerSecond.zero();
    public Temperature temperature = Celsius.zero();
    public Angle position = Radians.zero();

    public ClimberIOInputs() {
      OnboardLogger log = new OnboardLogger("Climber");
      log.registerBoolean("Motor Connected", () -> motorConnected);
      log.registerMeasurement("Supply Current", () -> supplyCurrent, Amps);
      log.registerMeasurement("Torque Current", () -> torqueCurrent, Amps);
      log.registerMeasurement("Stator Current", () -> statorCurrent, Amps);
      log.registerMeasurement("Voltage", () -> voltage, Volts);
      log.registerMeasurement("Temperature", () -> temperature, Celsius);
      log.registerMeasurement("Velocity", () -> velocity, RotationsPerSecond);
      log.registerMeasurement("Position", () -> position, Rotations);
    }
  }

  void setPosition(Angle climbLevel);
}

