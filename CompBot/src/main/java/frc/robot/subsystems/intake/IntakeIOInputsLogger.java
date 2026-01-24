package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;

import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;
import frc.robot.util.OnboardLogger;

public class IntakeIOInputsLogger {
    private final OnboardLogger oLogger;

    public IntakeIOInputsLogger(IntakeIOInputs inputs) {
        oLogger = new OnboardLogger("Intake");
        oLogger.registerBoolean("Motor Connected", () -> inputs.motorConnected);
        oLogger.registerMeasurment("Supply Current", () -> inputs.supplyCurrent, Amps);
        oLogger.registerMeasurment("Torque Current", () -> inputs.torqueCurrent, Amps);
        oLogger.registerMeasurment("Stator Current", () -> inputs.statorCurrent, Amps);
        oLogger.registerMeasurment("Voltage", () -> inputs.voltage, Volts);
        oLogger.registerMeasurment("Temperature", () -> inputs.temperature, Celsius);
    }
}
