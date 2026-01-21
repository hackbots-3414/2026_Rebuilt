package frc.robot.subsystems.Turret;

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

public class TurretIOHardware implements TurretIO {

    private final TalonFX m_turretMotor;

    private final DynamicMotionMagicVoltage m_control;

    private double m_reference = Double.NaN;

    private final CANcoder m_gear1CANcoder;
    private final CANcoder m_gear2CANcoder;
    private final CANcoder m_motorCANcoder;

    private final StatusSignal<Voltage> m_turretVoltageSignal;
    private final StatusSignal<Current> m_turretCurrentSignal;
    private final StatusSignal<Temperature> m_turretTempSignal;
    private final StatusSignal<AngularVelocity> m_turretVelocitySignal;
    private final StatusSignal<Angle> m_turretPositionSignal;

    public TurretIOHardware() {
        m_turretMotor = new TalonFX(TurretConstants.turretMotorID);
        m_turretMotor.getConfigurator().apply(TurretConstants.kMotorConfig);
        m_turretMotor.setPosition(0.0);

        m_gear1CANcoder = new CANcoder(TurretConstants.gear1CANcoderID);
        m_gear2CANcoder = new CANcoder(TurretConstants.gear2CANcoderID);
        m_motorCANcoder = new CANcoder(TurretConstants.motorCANcoderID);

        m_control = new DynamicMotionMagicVoltage(
                TurretConstants.kMaxSpeed,
                TurretConstants.kMaxAcceleration,
                TurretConstants.kMaxJerk);

        m_turretVoltageSignal = m_turretMotor.getMotorVoltage();
        m_turretCurrentSignal = m_turretMotor.getSupplyCurrent();
        m_turretTempSignal = m_turretMotor.getDeviceTemp();
        m_turretVelocitySignal = m_turretMotor.getVelocity();
        m_turretPositionSignal = m_turretMotor.getPosition();

    }

    public void setPosition(double reference) {
        m_turretMotor.setControl(m_control.withPosition(reference));
        m_reference = reference;
    }

    public void setVoltage(double voltage) {
        m_turretMotor.setVoltage(voltage);
    }

    public void enableLimits() {
        m_turretMotor.getConfigurator().apply(TurretConstants.kMotorConfig.SoftwareLimitSwitch);
    }

    public void disableLimits() {
        SoftwareLimitSwitchConfigs noLimits = new SoftwareLimitSwitchConfigs()
                .withForwardSoftLimitEnable(false)
                .withReverseSoftLimitEnable(false);
        m_turretMotor.getConfigurator().apply(noLimits);
    }

    public void calibrateZero() {
        m_turretMotor.setPosition(0.0);
    }

    public void updateInputs(TurretIOInputs inputs) {

        inputs.turretMotorConnected = BaseStatusSignal.isAllGood(
            m_turretVoltageSignal,
            m_turretCurrentSignal,
            m_turretTempSignal,
            m_turretVelocitySignal,
            m_turretPositionSignal);
        inputs.turretVoltage = m_turretVoltageSignal.getValueAsDouble();
        inputs.turretCurrent = m_turretCurrentSignal.getValueAsDouble();
        inputs.turretTemp = m_turretTempSignal.getValueAsDouble();
        inputs.turretVelocityRPS = m_turretVelocitySignal.getValueAsDouble();
        inputs.turretPosition = m_turretPositionSignal.getValueAsDouble();
        inputs.position = inputs.turretPosition;

        inputs.reference = m_reference;

        inputs.gear1CANcoder = m_gear1CANcoder.getAbsolutePosition().getValueAsDouble();
        inputs.gear2CANcoder = m_gear2CANcoder.getAbsolutePosition().getValueAsDouble();
        inputs.turretCANcoder = m_motorCANcoder.getAbsolutePosition().getValueAsDouble();

    }

}
