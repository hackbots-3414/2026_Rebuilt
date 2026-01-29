package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANrange;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;

public class IntakeIOHardware implements IntakeIO {
    private final TalonFX motorRoller;
    private final TalonFX motorDrop;
    private CANrange canrange;
    private Current lastCurrent = Amps.zero();
    private Voltage voltageRoller;
    private Voltage voltageDrop;
    private final TorqueCurrentFOC control = new TorqueCurrentFOC(0);

    public IntakeIOHardware() {
        motorRoller = new TalonFX(IntakeConstants.kMotorRollerID);
        motorRoller.getConfigurator().apply(IntakeConstants.kMotorConfig);
        canrange = new CANrange(IntakeConstants.kcanrangeID);

        motorDrop = new TalonFX(IntakeConstants.kMotorDropID);
        // Temporary, need to give a separate kMotorDropConfig
        motorDrop.getConfigurator().apply(IntakeConstants.kMotorConfig);
    }

    public void updateInputs(IntakeIOInputs inputs) {
        inputs.motorRollerConnected = BaseStatusSignal.isAllGood(
            motorRoller.getSupplyCurrent(false),
            motorRoller.getTorqueCurrent(false),
            motorRoller.getStatorCurrent(false),
            motorRoller.getMotorVoltage(false),
            motorRoller.getDeviceTemp(false),
            motorRoller.getVelocity(false)
        );

        inputs.motorDropConnected = BaseStatusSignal.isAllGood(
            motorDrop.getSupplyCurrent(false),
            motorDrop.getTorqueCurrent(false),
            motorDrop.getStatorCurrent(false),
            motorDrop.getMotorVoltage(false),
            motorDrop.getDeviceTemp(false),
            motorDrop.getVelocity(false)
        );

        inputs.supplyCurrent = motorRoller.getSupplyCurrent(false).getValue();
        inputs.torqueCurrent = motorRoller.getTorqueCurrent(false).getValue();
        inputs.statorCurrent = motorRoller.getStatorCurrent(false).getValue();
        inputs.voltageRoller = motorRoller.getMotorVoltage(false).getValue();
        inputs.temperature = motorRoller.getDeviceTemp(false).getValue();
        inputs.velocity = motorRoller.getVelocity(false).getValue();
        inputs.hasFuel = canrange.getDistance(false).getValueAsDouble() < IntakeConstants.canrangeMax;
    
        inputs.voltageDrop = motorDrop.getMotorVoltage(false).getValue();
    }

    public void setCurrent(Current current) {
        if(!current.equals(lastCurrent)) {
            motorRoller.setControl(control.withOutput(current));
            lastCurrent = current;
        }
    }

    public void setRollerVoltage(Voltage voltage){
        this.voltageRoller = voltage;
        motorRoller.setVoltage(voltage.magnitude());
    }

    public void setDropVoltage(Voltage voltage){
        this.voltageDrop = voltage;
        motorDrop.setVoltage(voltage.magnitude());
    }

}
