package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Robot;

public class IndexerIOSim implements IndexerIO {
    private final DCMotorSim motor;

    public IndexerIOSim() {
        motor = new DCMotorSim(
            LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
            DCMotor.getKrakenX60Foc(1),
            0.01,
            0.02);
    }

    public void updateInputs(IndexerIOInputs inputs) {
        motor.update(Robot.kDefaultPeriod);
        inputs.motorConnected = true;
        inputs.supplyCurrent = Amps.of(motor.getCurrentDrawAmps());
        inputs.voltage = Volts.of(motor.getInputVoltage());
        inputs.velocity = motor.getAngularVelocity();
    }

    public void setVoltage(Voltage voltage) {
        motor.setInputVoltage(voltage.baseUnitMagnitude());
    }

    public void stop() {
        motor.setInputVoltage(IndexerConstants.kStopVoltage.baseUnitMagnitude());
    }
}
