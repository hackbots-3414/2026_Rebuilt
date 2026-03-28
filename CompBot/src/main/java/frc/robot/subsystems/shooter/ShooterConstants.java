package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;

import java.util.List;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TorqueCurrentConfigs;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import frc.robot.aiming.AimMeasurement;

public final class ShooterConstants {
  protected static final int kMotor1Id = 53;
  protected static final int kMotor2Id = 54;

  protected static final AngularVelocity kRecoveryErrorThreshold = RotationsPerSecond.of(8);
  protected static final AngularVelocity kShootingErrorDetectionThreshold = RotationsPerSecond.of(4);

  protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
      .withSlot0(new Slot0Configs()
          .withKP(3414) // a great number
          .withKI(0)
          .withKD(0))

      .withMotionMagic(new MotionMagicConfigs()
          .withMotionMagicAcceleration(10.0))

      .withMotorOutput(new MotorOutputConfigs()
          .withPeakReverseDutyCycle(0)
          .withNeutralMode(NeutralModeValue.Coast)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withTorqueCurrent(new TorqueCurrentConfigs()
          .withPeakReverseTorqueCurrent(0.0)
          .withPeakForwardTorqueCurrent(80.0))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(80)
          .withStatorCurrentLimit(100));

  public static final Distance kRadius = Inches.of(2);
  public static final AngularVelocity kReverseVelocity = RotationsPerSecond.of(30.0);

  public static final MotorAlignmentValue kMotor2Alignment = MotorAlignmentValue.Aligned;

  public static final LinearVelocity kMaxLinearSpeed = MetersPerSecond.of(15.5);
  public static final AngularVelocity kMaxRotationalSpeed = RotationsPerSecond.of(85.0);

  public static final class HoodConstants {
    protected static final int kMotorID = 56;
    protected static final int kCANcoderId = 57;

    protected static final double kRatio = 0.0028888888888;

    protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
        .withFeedback(new FeedbackConfigs()
            .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
            .withFeedbackRemoteSensorID(kCANcoderId)
            .withSensorToMechanismRatio(155.0 / 15.0))

        .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
            .withForwardSoftLimitEnable(true)
            .withReverseSoftLimitEnable(true)
            .withForwardSoftLimitThreshold(0.065)
            .withReverseSoftLimitThreshold(0.0))

        .withSlot0(new Slot0Configs()
            .withKA(0.1)
            .withKS(0.3)
            .withKV(7)

            .withKP(60.0)
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
            .withSensorDirection(SensorDirectionValue.Clockwise_Positive)
            .withAbsoluteSensorDiscontinuityPoint(0.8) // We'll never get to this point
            .withMagnetOffset(-0.150146484375));

    protected static final int kSlot = 0;

    /** The position of the hood when its sensor reads zero */
    protected static final Angle kOffset = Degrees.of(18.0);
  }

  public static final List<AimMeasurement> scoringMeasurements = List.of(
      // valid TOF
      new AimMeasurement(Meters.of(1.70), Rotation2d.fromDegrees(72), 32, Seconds.of(0.962)),
      new AimMeasurement(Meters.of(2.41), Rotation2d.fromDegrees(70), 34, Seconds.of(1.006)),
      new AimMeasurement(Meters.of(2.93), Rotation2d.fromDegrees(67), 36, Seconds.of(1.016)),
      new AimMeasurement(Meters.of(3.33), Rotation2d.fromDegrees(65), 37, Seconds.of(1.014)),
      new AimMeasurement(Meters.of(3.81), Rotation2d.fromDegrees(61), 39, Seconds.of(1.07)),
      new AimMeasurement(Meters.of(4.29), Rotation2d.fromDegrees(59), 41, Seconds.of(1.064)),
      new AimMeasurement(Meters.of(4.77), Rotation2d.fromDegrees(59), 42 , Seconds.of(0.924)),
      // valid params
      new AimMeasurement(Meters.of(5.26), Rotation2d.fromDegrees(58), 45 , Seconds.of(0))
      // invalid everything
      );
      
  public static final List<AimMeasurement> feedingMeasurements = List.copyOf(scoringMeasurements);
}
