package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;
import java.util.Optional;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import frc.robot.util.StatusSignalUtil;

import yams.units.EasyCRTConfig;
import yams.units.EasyCRT;

public class TurretIOHardware implements TurretIO {

  private final TalonFX motor;

  private final CANcoder gear1CANcoder;
  private final CANcoder gear2CANcoder;

  private final DynamicMotionMagicVoltage control;

  private Angle reference = Radians.zero();

  private boolean calibrated = false;
  private EasyCRTConfig crtConfig;
  private EasyCRT crt;

  public TurretIOHardware() {
    motor = new TalonFX(TurretConstants.turretMotorID);
    motor.getConfigurator().apply(TurretConstants.kMotorConfig);
    motor.setPosition(0.0);

    gear1CANcoder = new CANcoder(TurretConstants.kGear1CANcoderID);
    gear2CANcoder = new CANcoder(TurretConstants.kGear2CANcoderID);

    gear1CANcoder.getConfigurator().apply(TurretConstants.kGear1CANcoderConfig);
    gear2CANcoder.getConfigurator().apply(TurretConstants.kGear2CANcoderConfig);

    control = new DynamicMotionMagicVoltage(
        TurretConstants.kMaxSpeed,
        TurretConstants.kMaxAcceleration,
        TurretConstants.kMaxJerk);

    StatusSignalUtil.registerRioSignals(
        motor.getMotorVoltage(false),
        motor.getSupplyCurrent(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false),
        motor.getPosition(false),
        gear1CANcoder.getAbsolutePosition(false),
        gear2CANcoder.getAbsolutePosition(false));

    crtConfig = new EasyCRTConfig(
        gear1CANcoder.getAbsolutePosition(false)::getValue,
        gear2CANcoder.getAbsolutePosition(false)::getValue)
            .withEncoderRatios(TurretConstants.kGearRatio1, TurretConstants.kGearRatio2);

    crt = new EasyCRT(crtConfig);
  }

  public void setPosition(Angle reference) {
    this.reference = reference;
    motor.setControl(control.withPosition(reference));
  }

  public void updateInputs(TurretIOInputs inputs) {
    inputs.motorConnected = BaseStatusSignal.isAllGood(
        motor.getMotorVoltage(false),
        motor.getSupplyCurrent(false),
        motor.getStatorCurrent(false),
        motor.getTorqueCurrent(false),
        motor.getDeviceTemp(false),
        motor.getVelocity(false),
        motor.getPosition(false));
    inputs.calibrated = calibrated;
    inputs.voltage = motor.getMotorVoltage(false).getValue();
    inputs.supplyCurrent = motor.getSupplyCurrent(false).getValue();
    inputs.statorCurrent = motor.getStatorCurrent(false).getValue();
    inputs.torqueCurrent = motor.getTorqueCurrent(false).getValue();
    inputs.temperature = motor.getDeviceTemp(false).getValue();
    inputs.velocity = motor.getVelocity(false).getValue();
    inputs.position = motor.getPosition(false).getValue();
    inputs.reference = reference;
  }

  public void calibrate() {
    Optional<Angle> totalPosition = crt.getAngleOptional();
    boolean isOk = totalPosition.isPresent();
    totalPosition.ifPresent(position -> motor.setPosition(position));
    calibrated |= isOk;
  }
}
