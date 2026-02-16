package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;

public final class ShooterConstants {
    protected static final int kMotor1Id = 53;
    protected static final int kMotor2Id = 54;

    protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
            .withSlot0(new Slot0Configs()
                    .withKA(0.6)
                    .withKS(0)
                    .withKV(0)

                    .withKP(8.0)
                    .withKI(0)
                    .withKD(0))

            .withMotionMagic(new MotionMagicConfigs()
                .withMotionMagicAcceleration(30.0))

            .withMotorOutput(new MotorOutputConfigs()
                    .withNeutralMode(NeutralModeValue.Coast)
                    .withInverted(InvertedValue.Clockwise_Positive))

            .withCurrentLimits(new CurrentLimitsConfigs()
                    .withSupplyCurrentLimitEnable(true)
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(100)
                    .withStatorLimit(125));

    public static final Distance kRadius = Inches.of(2);
    public static final AngularVelocity kReverseVelocity = RotationsPerSecond.of(30.0);

    public static final MotorAlignmentValue kMotor2Alignment = MotorAlignmentValue.Aligned;

    protected static final LinearVelocity kMaxLinearSpeed = MetersPerSecond.of(9.0);
    protected static final AngularVelocity kMaxRotationalSpeed = RotationsPerSecond.of(100.0);

    public static final class HoodConstants {
        protected static final int kMotorID = 56;
        protected static final int kCANcoderId = 57;

        protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
                .withFeedback(new FeedbackConfigs()
                        .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
                        .withFeedbackRemoteSensorID(kCANcoderId)
                        .withSensorToMechanismRatio(155.0/15.0))
                
                .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
                        .withForwardSoftLimitEnable(true)
                        .withReverseSoftLimitEnable(true)
                        .withForwardSoftLimitThreshold(0.065)
                        .withReverseSoftLimitThreshold(0.0))

                .withSlot0(new Slot0Configs()
                        .withKA(0.2)
                        .withKS(0)
                        .withKV(10)

                        .withKP(40.0)
                        .withKI(0)
                        .withKD(0))
                
                .withMotionMagic(new MotionMagicConfigs()
                        .withMotionMagicCruiseVelocity(3.0)
                        .withMotionMagicAcceleration(4))

                .withMotorOutput(new MotorOutputConfigs()
                        .withNeutralMode(NeutralModeValue.Brake)
                        .withInverted(InvertedValue.CounterClockwise_Positive))

                .withCurrentLimits(new CurrentLimitsConfigs()
                        .withSupplyCurrentLimitEnable(true)
                        .withStatorCurrentLimitEnable(true)
                        .withSupplyCurrentLimit(40)
                        .withStatorCurrentLimit(125));

          protected static final CANcoderConfiguration kCANcoderConfig = new CANcoderConfiguration()
            .withMagnetSensor(new MagnetSensorConfigs()
                .withAbsoluteSensorDiscontinuityPoint(0.7) // We'll never get to this point
                .withMagnetOffset(0.24755859375));


        protected static final int kSlot = 0;

        /** The position of the hood when its sensor reads zero */
        protected static final Angle kOffset = Degrees.of(18.0);
    }
}
