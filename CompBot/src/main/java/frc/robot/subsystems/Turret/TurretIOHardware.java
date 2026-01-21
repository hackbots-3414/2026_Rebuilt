package frc.robot.subsystems.Turret;

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
}
