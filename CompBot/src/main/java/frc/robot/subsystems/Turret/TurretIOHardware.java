package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import frc.robot.util.StatusSignalUtil;

public class TurretIOHardware implements TurretIO {

    private final TalonFX turretMotor;

    private final DynamicMotionMagicVoltage control;

    private Angle reference = Radians.zero();

    private final CANcoder gear1CANcoder;
    private final CANcoder gear2CANcoder;
    private final CANcoder motorCANcoder;

    public TurretIOHardware() {
        turretMotor = new TalonFX(TurretConstants.turretMotorID);
        turretMotor.getConfigurator().apply(TurretConstants.kMotorConfig);
        turretMotor.setPosition(0.0);

        gear1CANcoder = new CANcoder(TurretConstants.kGear1CANcoderID);
        gear2CANcoder = new CANcoder(TurretConstants.kGear2CANcoderID);
        motorCANcoder = new CANcoder(TurretConstants.kMotorCANcoderID);

        control = new DynamicMotionMagicVoltage(
                TurretConstants.kMaxSpeed,
                TurretConstants.kMaxAcceleration,
                TurretConstants.kMaxJerk);

        StatusSignalUtil.registerRioSignals(turretMotor.getMotorVoltage(false));
        StatusSignalUtil.registerRioSignals(turretMotor.getSupplyCurrent(false));
        StatusSignalUtil.registerRioSignals(turretMotor.getDeviceTemp(false));
        StatusSignalUtil.registerRioSignals(turretMotor.getVelocity(false));
        StatusSignalUtil.registerRioSignals(turretMotor.getPosition(false));

    }

    public void setPosition(Angle referenceAngle) {
        turretMotor.setControl(control.withPosition(referenceAngle));
        reference = referenceAngle;
    }

    public void enableLimits() {
        turretMotor.getConfigurator().apply(TurretConstants.kMotorConfig.SoftwareLimitSwitch);
    }

    public void disableLimits() {
        SoftwareLimitSwitchConfigs noLimits = new SoftwareLimitSwitchConfigs()
                .withForwardSoftLimitEnable(false)
                .withReverseSoftLimitEnable(false);
        turretMotor.getConfigurator().apply(noLimits);
    }

    public void calibrateZero() {
        turretMotor.setPosition(0.0);
    }

    public void updateInputs(TurretIOInputs inputs) {

        inputs.turretMotorConnected = BaseStatusSignal.isAllGood(        
            turretMotor.getMotorVoltage(false),
            turretMotor.getSupplyCurrent(false),
            turretMotor.getDeviceTemp(false),
            turretMotor.getVelocity(false),
            turretMotor.getPosition(false)
        );
        
        inputs.turretVoltage = turretMotor.getMotorVoltage().getValue();
        inputs.turretCurrent = turretMotor.getStatorCurrent().getValue();
        inputs.turretTemp = turretMotor.getDeviceTemp().getValue();
        inputs.turretVelocityRPS = turretMotor.getVelocity().getValue();
        inputs.turretPosition = turretMotor.getPosition().getValue();
        inputs.motorPosition = motorCANcoder.getAbsolutePosition().getValue();
        inputs.reference = reference;
    }

}
