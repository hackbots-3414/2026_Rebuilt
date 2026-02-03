package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Radians;
import java.util.Optional;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.util.StatusSignalUtil;
import yams.units.EasyCRT;
import yams.units.EasyCRTConfig;

public class TurretIOHardware implements TurretIO {

  private final TalonFX motor;

  private final CANcoder gear1CANcoder;
  private final CANcoder gear2CANcoder;

  private final DynamicMotionMagicTorqueCurrentFOC control;

  private Angle reference = Radians.zero();

  private boolean calibrated = false;
  private EasyCRTConfig crtConfig;
  private EasyCRT crt;

  public TurretIOHardware() {
    motor = new TalonFX(TurretConstants.kMotorId);
    motor.getConfigurator().apply(TurretConstants.kMotorConfig);
    motor.setPosition(0.0);

    gear1CANcoder = new CANcoder(TurretConstants.kEncoder1Id);
    gear2CANcoder = new CANcoder(TurretConstants.kEncoder2Id);

    gear1CANcoder.getConfigurator().apply(TurretConstants.kEncoder1Config);
    gear2CANcoder.getConfigurator().apply(TurretConstants.kEncoder2Config);

    control = new DynamicMotionMagicTorqueCurrentFOC(
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
      // Note: we explicitly choose to refresh these signals on calibration.
        gear1CANcoder.getAbsolutePosition()::getValue,
        gear2CANcoder.getAbsolutePosition()::getValue)
            .withEncoderRatios(TurretConstants.kEncoder1GearRatio, TurretConstants.kEncoder2GearRatio);

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
    if (!isOk) {
      DriverStation.reportError("Received not-good turret CRT solve: " + crt.getLastStatus(), false);
    }
    calibrated |= isOk;
  }
}
