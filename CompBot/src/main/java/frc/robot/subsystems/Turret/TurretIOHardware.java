package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Turret.TurretConstants.TurretCRTConstants;
import frc.robot.util.StatusSignalUtil;

public class TurretIOHardware implements TurretIO {

    private final TalonFX turretMotor;

    private final CANcoder gear1CANcoder;
    private final CANcoder gear2CANcoder;

    private final DynamicMotionMagicVoltage control;

    private Rotation2d reference = Rotation2d.kZero;

    private final SoftwareLimitSwitchConfigs noLimits = new SoftwareLimitSwitchConfigs()
                .withForwardSoftLimitEnable(false)
                .withReverseSoftLimitEnable(false);

    public TurretIOHardware() {
        turretMotor = new TalonFX(TurretConstants.turretMotorID);
        turretMotor.getConfigurator().apply(TurretConstants.kMotorConfig);
        turretMotor.setPosition(0.0);

        gear1CANcoder = new CANcoder(TurretConstants.kGear1CANcoderID);
        gear2CANcoder = new CANcoder(TurretConstants.kGear2CANcoderID);

        control = new DynamicMotionMagicVoltage(
                TurretConstants.kMaxSpeed,
                TurretConstants.kMaxAcceleration,
                TurretConstants.kMaxJerk);

        StatusSignalUtil.registerRioSignals(
            turretMotor.getMotorVoltage(false),
            turretMotor.getSupplyCurrent(false),
            turretMotor.getDeviceTemp(false),
            turretMotor.getVelocity(false),
            turretMotor.getPosition(false),
            gear1CANcoder.getAbsolutePosition(false),
            gear2CANcoder.getAbsolutePosition(false));
    }

    public void setPosition(Rotation2d referenceAngle) {
        turretMotor.setControl(control.withPosition(referenceAngle.getMeasure()));
        reference = referenceAngle;
    }

    public void updateInputs(TurretIOInputs inputs) {
        inputs.turretMotorConnected = BaseStatusSignal.isAllGood(
                turretMotor.getMotorVoltage(false),
                turretMotor.getSupplyCurrent(false),
                turretMotor.getDeviceTemp(false),
                turretMotor.getVelocity(false),
                turretMotor.getPosition(false));

        inputs.turretVoltage = turretMotor.getMotorVoltage(false).getValue();
        inputs.turretCurrent = turretMotor.getStatorCurrent(false).getValue();
        inputs.turretTemp = turretMotor.getDeviceTemp(false).getValue();
        inputs.turretVelocityRPS = turretMotor.getVelocity(false).getValue();
        inputs.position = turretMotor.getPosition(false).getValue();
        inputs.reference = reference.getMeasure();
    }

    public void calibrate() {
        double x12 = gear1CANcoder.getAbsolutePosition().getValueAsDouble();
        double x26 = gear2CANcoder.getAbsolutePosition().getValueAsDouble();

        double absolutePosition = crtSolve(x12, x26);
        turretMotor.setPosition(absolutePosition);
    }

    public static double crtSolve(double x12, double x26) {
        double best = Double.NaN;
        double bestDelta = 1e-1;

        for (int n = 0; n <= Math.ceil(1 / TurretCRTConstants.kG12); n++) {
            for (int m = 0;m < Math.ceil(1 / TurretCRTConstants.kG26); m ++) {
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
