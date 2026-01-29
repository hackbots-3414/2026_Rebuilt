package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANrange;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Current;
import frc.robot.util.StatusSignalUtil;

public class IntakeIOHardware implements IntakeIO {
  private final TalonFX motor;
  private final CANrange canrange;

  private final TorqueCurrentFOC control = new TorqueCurrentFOC(0);

  private Current lastCurrent = Amps.zero();

  public IntakeIOHardware() {
    motor = new TalonFX(IntakeConstants.kMotorID);
    motor.getConfigurator().apply(IntakeConstants.kMotorConfig);
    canrange = new CANrange(IntakeConstants.kcanrangeID);
    canrange.getConfigurator().apply(IntakeConstants.kCANrangeConfig);

    StatusSignalUtil.registerRioSignals(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false),

        canrange.getDistance(false),
        canrange.getIsDetected(false));
  }

  public void updateInputs(IntakeIOInputs inputs) {
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
    inputs.velocity = motor.getVelocity(false).getValue();

    inputs.canrangeDetected = BaseStatusSignal.isAllGood(
        canrange.getDistance(false),
        canrange.getIsDetected(false));

    inputs.canrangeDistance = canrange.getDistance(false).getValue();
    inputs.canrangeDetected = canrange.getIsDetected(false).getValue();
  }

  public void setCurrent(Current current) {
    if (!current.equals(lastCurrent)) {
      motor.setControl(control.withOutput(current));
      lastCurrent = current;
    }
  }
}
