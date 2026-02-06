package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Voltage;
import frc.robot.subsystems.indexer.IndexerConstants.FeederConstants;
import frc.robot.util.StatusSignalUtil;

public class IndexerIOHardware implements IndexerIO {
    private final TalonFX feedMotor;

    private Voltage lastFeedVoltage = Volts.zero();

    public IndexerIOHardware() {
        feedMotor = new TalonFX(FeederConstants.kMotorID);
        feedMotor.getConfigurator().apply(FeederConstants.kMotorConfig);

        StatusSignalUtil.registerRioSignals(
            feedMotor.getSupplyCurrent(false),
            feedMotor.getTorqueCurrent(false),
            feedMotor.getStatorCurrent(false),
            feedMotor.getMotorVoltage(false),
            feedMotor.getDeviceTemp(false),
            feedMotor.getVelocity(false)
        );
    }

    public void updateInputs(IndexerIOInputs inputs) {
        inputs.feedMotorConnected = BaseStatusSignal.isAllGood(
            feedMotor.getSupplyCurrent(false),
            feedMotor.getTorqueCurrent(false),
            feedMotor.getStatorCurrent(false),
            feedMotor.getMotorVoltage(false),
            feedMotor.getDeviceTemp(false),
            feedMotor.getVelocity(false)
        );
        inputs.feedSupplyCurrent = feedMotor.getSupplyCurrent(false).getValue();
        inputs.feedTorqueCurrent = feedMotor.getTorqueCurrent(false).getValue();
        inputs.feedStatorCurrent = feedMotor.getStatorCurrent(false).getValue();
        inputs.feedVoltage = feedMotor.getMotorVoltage(false).getValue();
        inputs.feedTemperature = feedMotor.getDeviceTemp(false).getValue();
        inputs.feedVelocity = feedMotor.getVelocity(false).getValue();
    }

    public void setFeedVoltage(Voltage voltage) {
        if (!voltage.equals(lastFeedVoltage)) {
            feedMotor.setControl(new VoltageOut(voltage));
            lastFeedVoltage = voltage;
        }
    }

    public void stop() {
        feedMotor.stopMotor();
    }
}
