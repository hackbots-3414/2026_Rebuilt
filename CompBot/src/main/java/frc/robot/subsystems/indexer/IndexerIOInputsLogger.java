package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import frc.robot.subsystems.indexer.IndexerIO.IndexerIOInputs;
import frc.robot.util.OnboardLogger;

public class IndexerIOInputsLogger {
    private final OnboardLogger oLogger;

    public IndexerIOInputsLogger(IndexerIOInputs inputs) {
        oLogger = new OnboardLogger("Indexer");
        oLogger.registerBoolean("Motor Connected", () -> inputs.motorConnected);
        oLogger.registerMeasurment("Supply Current", () -> inputs.supplyCurrent, Amps);
        oLogger.registerMeasurment("Torque Current", () -> inputs.torqueCurrent, Amps);
        oLogger.registerMeasurment("Stator Current", () -> inputs.statorCurrent, Amps);
        oLogger.registerMeasurment("Voltage", () -> inputs.voltage, Volts);
        oLogger.registerMeasurment("Temperature", () -> inputs.temperature, Celsius);
        oLogger.registerMeasurment("Velocity", () -> inputs.velocity, RotationsPerSecond);
    }
}

