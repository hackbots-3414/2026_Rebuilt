package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANrange;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;

public class IntakeIOHardware implements IntakeIO {
    private final TalonFX motor;
    private CANrange canrange;
    private Current lastCurrent = Amps.zero();
    private Voltage voltage;
    private final TorqueCurrentFOC control = new TorqueCurrentFOC(0);

    public IntakeIOHardware() {
        motor = new TalonFX(IntakeConstants.kMotorID);
        motor.getConfigurator().apply(IntakeConstants.kMotorConfig);
         canrange = new CANrange(IntakeConstants.kcanrangeID);
    }

    public void updateInputs(IntakeIOInputs inputs) {
        inputs.motorConnected = BaseStatusSignal.isAllGood(
            motor.getSupplyCurrent(false),
            motor.getTorqueCurrent(false),
            motor.getStatorCurrent(false),
            motor.getMotorVoltage(false),
            motor.getDeviceTemp(false),
            motor.getVelocity(false)
        );

        inputs.supplyCurrent = motor.getSupplyCurrent(false).getValue();
        inputs.torqueCurrent = motor.getTorqueCurrent(false).getValue();
        inputs.statorCurrent = motor.getStatorCurrent(false).getValue();
        inputs.voltage = motor.getMotorVoltage(false).getValue();
        inputs.temperature = motor.getDeviceTemp(false).getValue();
        inputs.velocity = motor.getVelocity(false).getValue();
        inputs.hasFuel = canrange.getDistance(false).getValueAsDouble() < IntakeConstants.canrangeMax;
    }

    public void setCurrent(Current current) {
        if(!current.equals(lastCurrent)) {
            motor.setControl(control.withOutput(current));
            lastCurrent = current;
        }
    }

    public void setVoltage(Voltage voltage){
        this.voltage = voltage;
    }

}
