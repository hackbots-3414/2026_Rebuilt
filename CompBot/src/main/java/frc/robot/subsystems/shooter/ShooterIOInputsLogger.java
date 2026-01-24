package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import frc.robot.subsystems.shooter.ShooterIO.ShooterIOInputs;
import frc.robot.util.OnboardLogger;

public class ShooterIOInputsLogger {
    private final OnboardLogger oLogger;

    public ShooterIOInputsLogger(ShooterIOInputs inputs) {
        oLogger = new OnboardLogger("Shooter");
        oLogger.registerBoolean("Motor Connected", () -> inputs.motorConnected);
        oLogger.registerMeasurment("Supply Current", () -> inputs.supplyCurrent, Amps);
        oLogger.registerMeasurment("Torque Current", () -> inputs.torqueCurrent, Amps);
        oLogger.registerMeasurment("Stator Current", () -> inputs.statorCurrent, Amps);
        oLogger.registerMeasurment("Voltage", () -> inputs.voltage, Volts);
        oLogger.registerMeasurment("Temperature", () -> inputs.temperature, Celsius);
        oLogger.registerMeasurment("Velocity", () -> inputs.velocity, RotationsPerSecond);
    }
}
