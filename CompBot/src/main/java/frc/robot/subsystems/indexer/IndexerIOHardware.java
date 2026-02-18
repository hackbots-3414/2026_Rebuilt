package frc.robot.subsystems.indexer;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.subsystems.indexer.IndexerConstants.FeederConstants;
import frc.robot.subsystems.indexer.IndexerConstants.SpindexerConstants;
import frc.robot.util.StatusSignalUtil;

public class IndexerIOHardware implements IndexerIO {
  private final TalonFX feeder;
  private final TalonFX spindexer;

  private final VoltageOut feederControl = new VoltageOut(0.0);
  private final VoltageOut spindexerControl = new VoltageOut(0.0);

  public IndexerIOHardware() {
    feeder = new TalonFX(FeederConstants.kMotorId);
    feeder.getConfigurator().apply(FeederConstants.kMotorConfig);

    spindexer = new TalonFX(SpindexerConstants.kMotorId);
    spindexer.getConfigurator().apply(SpindexerConstants.kMotorConfig);

    StatusSignalUtil.registerRioSignals(
        feeder.getSupplyCurrent(false),
        feeder.getTorqueCurrent(false),
        feeder.getStatorCurrent(false),
        feeder.getMotorVoltage(false),
        feeder.getDeviceTemp(false),
        feeder.getVelocity(false),

        spindexer.getSupplyCurrent(false),
        spindexer.getTorqueCurrent(false),
        spindexer.getStatorCurrent(false),
        spindexer.getMotorVoltage(false),
        spindexer.getDeviceTemp(false),
        spindexer.getVelocity(false));
  }

  public void updateInputs(IndexerIOInputs inputs) {
    inputs.feedMotorConnected = BaseStatusSignal.isAllGood(
        feeder.getSupplyCurrent(false),
        feeder.getTorqueCurrent(false),
        feeder.getStatorCurrent(false),
        feeder.getMotorVoltage(false),
        feeder.getDeviceTemp(false),
        feeder.getVelocity(false));
    inputs.feedSupplyCurrent = feeder.getSupplyCurrent(false).getValue();
    inputs.feedTorqueCurrent = feeder.getTorqueCurrent(false).getValue();
    inputs.feedStatorCurrent = feeder.getStatorCurrent(false).getValue();
    inputs.feedVoltage = feeder.getMotorVoltage(false).getValue();
    inputs.feedTemperature = feeder.getDeviceTemp(false).getValue();
    inputs.feedVelocity = feeder.getVelocity(false).getValue();

    inputs.spindexerMotorConnected = BaseStatusSignal.isAllGood(
        spindexer.getSupplyCurrent(false),
        spindexer.getTorqueCurrent(false),
        spindexer.getStatorCurrent(false),
        spindexer.getMotorVoltage(false),
        spindexer.getDeviceTemp(false),
        spindexer.getVelocity(false));
    inputs.spindexerSupplyCurrent = spindexer.getSupplyCurrent(false).getValue();
    inputs.spindexerTorqueCurrent = spindexer.getTorqueCurrent(false).getValue();
    inputs.spindexerStatorCurrent = spindexer.getStatorCurrent(false).getValue();
    inputs.spindexerVoltage = spindexer.getMotorVoltage(false).getValue();
    inputs.spindexerTemperature = spindexer.getDeviceTemp(false).getValue();
    inputs.spindexerVelocity = spindexer.getVelocity(false).getValue();
  }

  public void setFeedVoltage(Voltage voltage) {
    if (!voltage.equals(feederControl.getOutputMeasure())) {
      feeder.setControl(feederControl.withOutput(voltage));
    }
  }

  public void setSpindexerVoltage(Voltage voltage) {
    if (!voltage.equals(spindexerControl.getOutputMeasure())) {
      feeder.setControl(spindexerControl.withOutput(voltage));
    }
  }

  public void stop() {
    feeder.stopMotor();
  }
}
