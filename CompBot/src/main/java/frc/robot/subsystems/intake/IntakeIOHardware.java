package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Current;

public class IntakeIOHardware implements IntakeIO {
    private final TalonFX motor;
    private Current lastCurrent = Amps.zero();
    private final TorqueCurrentFOC control = new TorqueCurrentFOC(0);

    public IntakeIOHardware() {
        motor = new TalonFX(IntakeConstants.kMotorId);
        motor.getConfigurator().apply(IntakeConstants.kMotorConfig);
    }

    public void updateInputs(IntakeIOInputs inputs) {
        inputs.motorConnected = BaseStatusSignal.isAllGood(
            motor.getSupplyCurrent(false),
            motor.getTorqueCurrent(false),
            motor.getStatorCurrent(false),
            motor.getMotorVoltage(false),
            motor.getDeviceTemp(false)
        );

        inputs.supplyCurrent = motor.getSupplyCurrent(false).getValue();
        inputs.torqueCurrent = motor.getTorqueCurrent(false).getValue();
        inputs.statorCurrent = motor.getStatorCurrent(false).getValue();
        inputs.voltage = motor.getMotorVoltage(false).getValue();
        inputs.temperature = motor.getDeviceTemp(false).getValue();
    }

    public void setCurrent(Current current) {
        if(!current.equals(lastCurrent)) {
            motor.setControl(control.withOutput(current));
            lastCurrent = current;
        }
    }
}
