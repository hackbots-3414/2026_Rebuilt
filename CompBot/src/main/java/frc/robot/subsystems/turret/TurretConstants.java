package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Revolutions;
import static edu.wpi.first.units.Units.Rotations;
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
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.units.measure.Angle;

public class TurretConstants {
  protected static final int kMotorId = 48;
  protected static final int kEncoder1Id = 49;
  protected static final int kEncoder2Id = 50;

  protected static final double kSupplyCurrentLimit = 100;

  protected static final Angle kHomePosition = Revolutions.of(0);
  /**
   * The position measurement of the turret such that the turret points directly forward on the
   * robot. For example, if this value was 30 degrees, then setting the turret's position to 30
   * degrees would result in the turret pointing forwards on the robot.
   */
  protected static final Angle kForwards = Revolutions.of(0);

  // MotionMagic configuration
  protected static final double kGearRatio = 30.0;
  protected static final double kMaxSpeed = 40;
  protected static final double kMaxAcceleration = 70;
  protected static final double kMaxJerk = 480;

  protected static final Angle kTolerance = Degrees.of(1);

  // CRT-focused constants
  protected static final double kEncoder1Offset = -0.11474609375;
  protected static final double kEncoder2Offset = -0.634521484375;
  protected static final double kEncoder1GearRatio = 72.0 / 12.0;
  protected static final double kEncoder2GearRatio = (72.0 * 25.0) / (12.0 * 27.0);

  // These parameters define the range of valid angles for the turret
  protected static final Angle kMinAngle = Rotations.of(-0.5);
  protected static final Angle kMaxAngle = Rotations.of(0.5);

  // CANcoder configurations
  protected static final CANcoderConfiguration kEncoder1Config = new CANcoderConfiguration()
      .withMagnetSensor(new MagnetSensorConfigs()
          .withAbsoluteSensorDiscontinuityPoint(1.0)
          .withMagnetOffset(kEncoder1Offset));

  protected static final CANcoderConfiguration kEncoder2Config = new CANcoderConfiguration()
      .withMagnetSensor(new MagnetSensorConfigs()
          .withAbsoluteSensorDiscontinuityPoint(1.0)
          .withMagnetOffset(kEncoder2Offset)
          .withSensorDirection(SensorDirectionValue.Clockwise_Positive));

  // Motor configuration
  protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Coast)
          .withInverted(InvertedValue.CounterClockwise_Positive))

      .withFeedback(new FeedbackConfigs()
          .withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor)
          .withSensorToMechanismRatio(kGearRatio))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withSupplyCurrentLimit(kSupplyCurrentLimit))

      .withSlot0(new Slot0Configs()
          .withKP(35)
          .withKI(0)
          .withKD(0.1)
          .withKS(0.6)
          .withKV(2.5)
          .withKA(0))

      .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
          .withForwardSoftLimitEnable(true)
          .withForwardSoftLimitThreshold(kMaxAngle)
          .withReverseSoftLimitEnable(true)
          .withReverseSoftLimitThreshold(kMinAngle))

      .withMotionMagic(new MotionMagicConfigs()
          .withMotionMagicCruiseVelocity(kMaxSpeed)
          .withMotionMagicAcceleration(kMaxAcceleration)
          .withMotionMagicJerk(kMaxJerk));

  /** The turret's relative position on the robot */
  public static final Transform3d kOffset = new Transform3d(
      -0.11,
      0.11,
      0.512,
      Rotation3d.kZero);
}
