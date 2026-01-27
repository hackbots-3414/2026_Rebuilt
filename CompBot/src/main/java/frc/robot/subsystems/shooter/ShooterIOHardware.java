package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.hardware.TalonFX;

import frc.robot.subsystems.shooter.ShooterConstants.FlywheelConstants;
import frc.robot.subsystems.shooter.ShooterConstants.HoodConstants;
import frc.robot.util.StatusSignalUtil;

public class ShooterIOHardware implements ShooterIO {
  private final TalonFX flywheelMotor;
  private final TalonFX hoodMotor;

  private AngularVelocity lastVelocity = RotationsPerSecond.zero();
  private final MotionMagicVelocityTorqueCurrentFOC flywheelControl =
      new MotionMagicVelocityTorqueCurrentFOC(0)
          .withAcceleration(FlywheelConstants.kAcceleration);
  private final DynamicMotionMagicTorqueCurrentFOC hoodControl =
      new DynamicMotionMagicTorqueCurrentFOC(Radians.zero(), HoodConstants.kVelocity, HoodConstants.kAcceleration)
          .withSlot(ShooterConstants.HoodConstants.kSlot);

  public ShooterIOHardware() {
    flywheelMotor = new TalonFX(FlywheelConstants.kMotorID);
    flywheelMotor.getConfigurator().apply(FlywheelConstants.kMotorConfig);

    hoodMotor = new TalonFX(HoodConstants.kMotorID);
    hoodMotor.getConfigurator().apply(HoodConstants.kMotorConfig);


    StatusSignalUtil.registerRioSignals(
        flywheelMotor.getSupplyCurrent(false),
        flywheelMotor.getTorqueCurrent(false),
        flywheelMotor.getStatorCurrent(false),
        flywheelMotor.getMotorVoltage(false),
        flywheelMotor.getDeviceTemp(false),
        flywheelMotor.getVelocity(false),

        hoodMotor.getSupplyCurrent(false),
        hoodMotor.getTorqueCurrent(false),
        hoodMotor.getStatorCurrent(false),
        hoodMotor.getMotorVoltage(false),
        hoodMotor.getDeviceTemp(false),
        hoodMotor.getVelocity(false),
        hoodMotor.getPosition(false));
  }

  public void updateInputs(ShooterIOInputs inputs) {
    inputs.flywheelMotorConnected = BaseStatusSignal.isAllGood(
        flywheelMotor.getSupplyCurrent(false),
        flywheelMotor.getTorqueCurrent(false),
        flywheelMotor.getStatorCurrent(false),
        flywheelMotor.getMotorVoltage(false),
        flywheelMotor.getDeviceTemp(false),
        flywheelMotor.getVelocity(false),

        hoodMotor.getSupplyCurrent(false),
        hoodMotor.getTorqueCurrent(false),
        hoodMotor.getStatorCurrent(false),
        hoodMotor.getMotorVoltage(false),
        hoodMotor.getDeviceTemp(false),
        hoodMotor.getVelocity(false),
        hoodMotor.getPosition(false));

    inputs.flywheelSupplyCurrent = flywheelMotor.getSupplyCurrent(false).getValue();
    inputs.flywheelTorqueCurrent = flywheelMotor.getTorqueCurrent(false).getValue();
    inputs.flywheelStatorCurrent = flywheelMotor.getStatorCurrent(false).getValue();
    inputs.flywheelVoltage = flywheelMotor.getMotorVoltage(false).getValue();
    inputs.flywheelTemperature = flywheelMotor.getDeviceTemp(false).getValue();
    inputs.flywheelVelocity = flywheelMotor.getVelocity(false).getValue();

    inputs.hoodSupplyCurrent = hoodMotor.getSupplyCurrent(false).getValue();
    inputs.hoodTorqueCurrent = hoodMotor.getTorqueCurrent(false).getValue();
    inputs.hoodStatorCurrent = hoodMotor.getStatorCurrent(false).getValue();
    inputs.hoodVoltage = hoodMotor.getMotorVoltage(false).getValue();
    inputs.hoodTemperature = hoodMotor.getDeviceTemp(false).getValue();
    inputs.hoodVelocity = hoodMotor.getVelocity(false).getValue();
    inputs.hoodAngle = hoodMotor.getPosition(false).getValue();
  }

  public void setVelocity(AngularVelocity velocity) {
    if (!velocity.equals(lastVelocity)) {
      flywheelMotor.setControl(flywheelControl.withVelocity(velocity));
      lastVelocity = velocity;
    }
  }

  public void setAngle(Angle angle) {
    hoodMotor.setControl(hoodControl.withPosition(angle));
  }
}
