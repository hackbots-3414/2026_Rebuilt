package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.hardware.TalonFX;

import frc.robot.subsystems.shooter.ShooterConstants.FlywheelConstants;
import frc.robot.subsystems.shooter.ShooterConstants.HoodConstants;
import frc.robot.util.StatusSignalUtil;

public class ShooterIOHardware implements ShooterIO {
  private final TalonFX motor;

  private AngularVelocity lastVelocity = RotationsPerSecond.zero();
  private final MotionMagicVelocityTorqueCurrentFOC flywheelControl =
      new MotionMagicVelocityTorqueCurrentFOC(0)
          .withAcceleration(FlywheelConstants.kAcceleration);
  private final MotionMagicTorqueCurrentFOC hoodControl =
      new MotionMagicTorqueCurrentFOC(0)
          .withSlot(ShooterConstants.HoodConstants.kSlot);

  public ShooterIOHardware() {
    motor = new TalonFX(FlywheelConstants.kMotorID);
    motor.getConfigurator().apply(FlywheelConstants.kMotorConfig);

    StatusSignalUtil.registerRioSignals(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false));
  }

  public void updateInputs(ShooterIOInputs inputs) {
    inputs.flywheelMotorConnected = BaseStatusSignal.isAllGood(
        motor.getSupplyCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getStatorCurrent(false),
        motor.getMotorVoltage(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false));
    inputs.flywheelSupplyCurrent = motor.getSupplyCurrent(false).getValue();
    inputs.flywheelTorqueCurrent = motor.getTorqueCurrent(false).getValue();
    inputs.flywheelStatorCurrent = motor.getStatorCurrent(false).getValue();
    inputs.flywheelVoltage = motor.getMotorVoltage(false).getValue();
    inputs.flywheelTemperature = motor.getDeviceTemp(false).getValue();
    inputs.flywheelVelocity = motor.getVelocity(false).getValue();
  }

  public void setVelocity(AngularVelocity velocity) {
    if (!velocity.equals(lastVelocity)) {
      motor.setControl(flywheelControl.withVelocity(velocity));
      lastVelocity = velocity;
    }
  }

  public void setAngle(Angle angle) {
    motor.setControl(hoodControl.withPosition(angle));
  }
}
