package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
        inputs.turretPosition = Radians.of(getTurretAngleRadians(gear1CANcoder.getAbsolutePosition().getValueAsDouble(), gear2CANcoder.getAbsolutePosition().getValueAsDouble()));
        inputs.motorPosition = motorCANcoder.getAbsolutePosition().getValue();
        inputs.reference = reference;
    }


    /** 
     * @param gear1position Position off of the 28 tooth gear as read by the CANcoder on that axle
     * @param gear2position Position off of the 26 tooth gear as read by the CANcoder on that axle
     * @return: Turret position in radians in relation to robot, assuming that the opposite from center of the turret's blind spot is 0
     * @throws IllegalArgumentException Thrown if the turret location is bigger than the turret size
     */
    private double getTurretAngleRadians(double gear1Position, double gear2Position) throws IllegalArgumentException {        
        
        double g12 = (double) TurretConstants.kGear3Size/TurretConstants.kTurretSize;
        double g26 = (double) TurretConstants.kGear3Size*TurretConstants.kGear2Size/(TurretConstants.kTurretSize*TurretConstants.kGear1Size);
        int alpha = TurretConstants.kAlpha;

        double x12 = gear1Position;
        double x26 = gear2Position;

        double a1 = alpha*g12;
        double a2 = alpha*g26;
 
        for (double n = 0; n < 100*g12; n++) {
            if (((a1*n+x12*a1)-(x26*a2)) % a2 == 0) {
                return g12*(n+x12) *2*Math.PI;
            }
        }
        
        throw new IllegalArgumentException("Current turret encoders reflect impossible turret positions.");

    }

}
