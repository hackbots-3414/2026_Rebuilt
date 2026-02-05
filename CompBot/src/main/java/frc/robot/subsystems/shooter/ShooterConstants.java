package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecondPerSecond;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;

public final class ShooterConstants {
    protected static final int kMotor1Id = 53;
    protected static final int kMotor2Id = 54;

    protected static final double kSupplyCurrentLimit = 40.0;
    protected static final double kStatorCurrentLimit = 125.0;

    protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
            .withSlot0(new Slot0Configs()
                    .withKA(1.5)
                    .withKS(0)
                    .withKV(0)

                    .withKP(0)
                    .withKI(0)
                    .withKD(0))

            .withMotorOutput(new MotorOutputConfigs()
                    .withNeutralMode(NeutralModeValue.Coast)
                    .withInverted(InvertedValue.Clockwise_Positive))

            .withCurrentLimits(new CurrentLimitsConfigs()
                    .withSupplyCurrentLimitEnable(true)
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(kSupplyCurrentLimit)
                    .withStatorCurrentLimit(kStatorCurrentLimit));

    public static final AngularAcceleration kAcceleration = RotationsPerSecondPerSecond.zero();
    public static final Distance kRadius = Inches.of(2);
    public static final AngularVelocity kReverseVelocity = RotationsPerSecond.zero();

    public static final MotorAlignmentValue kFlip2 = MotorAlignmentValue.Aligned;

    protected static final double kSpeedTransferPercentage = 0.2;

    public static final class HoodConstants {
        protected static final int kMotorID = 56;
        protected static final int kCANcoderId = 57;

        protected static final double kSupplyCurrentLimit = 40.0;
        protected static final double kStatorCurrentLimit = 125.0;

        protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
                .withFeedback(new FeedbackConfigs()
                        .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
                        .withFeedbackRemoteSensorID(kCANcoderId)
                        .withSensorToMechanismRatio(155.0/15.0))
                
                .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
                        .withForwardSoftLimitEnable(true)
                        .withReverseSoftLimitEnable(true)
                        .withForwardSoftLimitThreshold(0.06)
                        .withReverseSoftLimitThreshold(0.0))

                .withSlot0(new Slot0Configs()
                        .withKA(0.0)
                        .withKS(0)
                        .withKV(0)

                        .withKP(0.0)
                        .withKI(0)
                        .withKD(0))

                .withMotorOutput(new MotorOutputConfigs()
                        .withNeutralMode(NeutralModeValue.Brake)
                        .withInverted(InvertedValue.CounterClockwise_Positive))

                .withCurrentLimits(new CurrentLimitsConfigs()
                        .withSupplyCurrentLimitEnable(true)
                        .withStatorCurrentLimitEnable(true)
                        .withSupplyCurrentLimit(kSupplyCurrentLimit)
                        .withStatorCurrentLimit(kStatorCurrentLimit));

          protected static final CANcoderConfiguration kCANcoderConfig = new CANcoderConfiguration()
            .withMagnetSensor(new MagnetSensorConfigs()
                .withAbsoluteSensorDiscontinuityPoint(1.0)
                .withMagnetOffset(-0.144043));


        protected static final int kSlot = 0;
        protected static final AngularVelocity kVelocity = RotationsPerSecond.zero();
        protected static final AngularAcceleration kAcceleration = RotationsPerSecondPerSecond.zero();
    }
}
