package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.StatusSignalUtil;

public class ClimberIOHardware implements ClimberIO {
  private final TalonFX motor;

  private Voltage lastVoltage = Volts.zero();

  public ClimberIOHardware() {
    motor = new TalonFX(ClimberConstants.kMotorID);
    motor.getConfigurator().apply(ClimberConstants.kMotorConfig);

    StatusSignalUtil.registerRioSignals(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false));
  }

  public void updateInputs(ClimberIOInputs inputs) {
    inputs.motorConnected = BaseStatusSignal.isAllGood(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false));
    inputs.supplyCurrent = motor.getSupplyCurrent(false).getValue();
    inputs.torqueCurrent = motor.getTorqueCurrent(false).getValue();
    inputs.statorCurrent = motor.getStatorCurrent(false).getValue();
    inputs.voltage = motor.getMotorVoltage(false).getValue();
    inputs.temperature = motor.getDeviceTemp(false).getValue();
  }

  public void setVoltage(Voltage voltage) {
    if (!voltage.equals(lastVoltage)) {
      motor.setControl(new VoltageOut(voltage));
      lastVoltage = voltage;
    }
  }
}

