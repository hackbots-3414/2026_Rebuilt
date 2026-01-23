package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Angle;
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
    private double getTurretAngleRadians(double gear1position, double gear2position) throws IllegalArgumentException {
        
        //Changing gear names to match video so we can follow the video
        int m1 = TurretConstants.kGear1Size;
        int m2 = TurretConstants.kGear2Size;

        //Turn CANcoder's 0 to 1 measurement into teeth.
        int a1 = (int) Math.round(gear1position * TurretConstants.kGear1Size);
        int a2 = (int) Math.round(gear2position * TurretConstants.kGear2Size);

        //Calculating M, M1, M2, and their inverses.
        int M = m1*m2;
        int M1 = M/m1;
        int M2 = M/m2;

        int inverseM1 = 0;
        int inverseM2 = 0;

        //Going to m1 because the lowest possible value of inverseM1 to get a remainder of 1 is going to be less than m1.
        for (int i = 0; i <= m1; i++) {
            if ((M1 *i) % m1 == 1) {
                inverseM1 = i;
                break;
            }
        }

        for (int i = 0; i <= m2; i++) {
            if ((M2 *i) % m2 == 1) {
                inverseM2 = i;
                break;
            }
        }
        double turretPositionInTeeth = (a1*M1*inverseM1 + a2*M2*inverseM2)%M;

        if (turretPositionInTeeth > TurretConstants.kTurretSize) {
            throw new IllegalArgumentException("Current turret encoders reflect value over possible turret positions.");
        }

        double turretPositionInRadians = (turretPositionInTeeth * 2 * Math.PI / TurretConstants.kTurretSize) - Math.PI;

        return turretPositionInRadians;
    }

}
