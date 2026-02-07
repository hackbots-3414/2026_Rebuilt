package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Robot;

public class IndexerIOSim implements IndexerIO {
  private final DCMotorSim feeder;
  private final DCMotorSim spindexer;

  public IndexerIOSim() {
    feeder = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
    spindexer = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
  }

  public void updateInputs(IndexerIOInputs inputs) {
    feeder.update(Robot.kDefaultPeriod);
    spindexer.update(Robot.kDefaultPeriod);

    inputs.feedMotorConnected = true;
    inputs.feedSupplyCurrent = Amps.of(feeder.getCurrentDrawAmps());
    inputs.feedVoltage = Volts.of(feeder.getInputVoltage());
    inputs.feedVelocity = feeder.getAngularVelocity();
  }

  public void setFeedVoltage(Voltage voltage) {
    feeder.setInputVoltage(voltage.baseUnitMagnitude());
  }

  public void setSpindexerVoltage(Voltage voltage) {}
}
