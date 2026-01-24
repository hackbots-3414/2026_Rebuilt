package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.RotationsPerSecond;
import edu.wpi.first.units.measure.AngularVelocity;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.hardware.TalonFX;

import frc.robot.util.StatusSignalUtil;

public class ShooterIOHardware implements ShooterIO {
  private final TalonFX motor;

  private AngularVelocity lastVelocity = RotationsPerSecond.zero();
  private final MotionMagicVelocityTorqueCurrentFOC control =
      new MotionMagicVelocityTorqueCurrentFOC(0)
          .withAcceleration(ShooterConstants.kAcceleration);

  public ShooterIOHardware() {
    motor = new TalonFX(ShooterConstants.kMotorID);
    motor.getConfigurator().apply(ShooterConstants.kMotorConfig);

    StatusSignalUtil.registerRioSignals(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false));
  }

  public void updateInputs(ShooterIOInputs inputs) {
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
  }

  public void setVelocity(AngularVelocity velocity) {
    if (!velocity.equals(lastVelocity)) {
      motor.setControl(control.withVelocity(velocity));
      lastVelocity = velocity;
    }
  }
}
