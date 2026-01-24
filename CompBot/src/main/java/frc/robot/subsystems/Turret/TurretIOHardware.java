package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import frc.robot.subsystems.Turret.TurretConstants.TurretCRTConstants;
import frc.robot.util.StatusSignalUtil;

public class TurretIOHardware implements TurretIO {

  private final TalonFX motor;

  private final CANcoder gear1CANcoder;
  private final CANcoder gear2CANcoder;

  private final DynamicMotionMagicVoltage control;

  private Angle reference = Radians.zero();

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
  }

  private boolean calibrated = false;

  public void setPosition(Rotation2d referenceAngle) {
    reference = referenceAngle.getMeasure();
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
    double x12 = gear1CANcoder.getAbsolutePosition().getValueAsDouble();
    double x26 = gear2CANcoder.getAbsolutePosition().getValueAsDouble();
    // Calcuate absolute position
    double absolutePosition = crtSolve(x12, x26);
    boolean isOk = absolutePosition != Double.NaN;
    if (isOk)
      motor.setPosition(absolutePosition);
    calibrated |= isOk;
  }

  public static double crtSolve(double x12, double x26) {
    double best = Double.NaN;
    double bestDelta = 1e-1;

    for (int n = 0; n <= Math.ceil(1 / TurretCRTConstants.kG12); n++) {
      for (int m = 0; m < Math.ceil(1 / TurretCRTConstants.kG26); m++) {
        double xFromN = TurretCRTConstants.kG12 * (n + x12);
        double xFromM = TurretCRTConstants.kG26 * (m + x26);
        double delta = Math.abs(xFromN - xFromM);
        if (delta < bestDelta) {
          bestDelta = delta;
          best = (xFromN + xFromM) / 2.0;
        }
      }
    }
    return best;
  }
}
