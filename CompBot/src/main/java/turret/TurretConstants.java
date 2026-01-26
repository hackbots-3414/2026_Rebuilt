package turret;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Revolutions;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.units.measure.Angle;

public class TurretConstants {
  protected static final Transform2d kTurretPosition = Transform2d.kZero;

  protected static final int kMotorId = 48;
  protected static final int kEncoder1Id = 49;
  protected static final int kEncoder2Id = 50;

  protected static final double kSupplyCurrentLimit = 100;

  protected static final Angle kHomePosition = Revolutions.of(0.5);
  /**
   * The position measurement of the turret such that the turret points directly forward on the
   * robot. For example, if this value was 30 degrees, then setting the turret's position to 30
   * degrees would result in the turret pointing forwards on the robot.
   */
  protected static final Angle kTrackingOffset = Revolutions.of(0.25);

  // MotionMagic configuration
  protected static final double kGearRatio = 38.46;
  protected static final double kMaxSpeed = 32;
  protected static final double kMaxAcceleration = 48;
  protected static final double kMaxJerk = 480;

  protected static final Angle kTolerance = Degrees.of(1);

  // CRT-focused constants
  protected static final double kEncoder1Offset = -0.352051;
  protected static final double kEncoder2Offset = -0.531006;
  protected static final double kEncoder1GearRatio = 100.0 / 12.0;
  protected static final double kEncoder2GearRatio = (100 * 28.0) / (12.0 * 26.0);


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
          .withKP(50)
          .withKI(0)
          .withKD(0)
          .withKS(0.125)
          .withKV(0)
          .withKA(0))

      .withMotionMagic(new MotionMagicConfigs()
          .withMotionMagicCruiseVelocity(kMaxSpeed)
          .withMotionMagicAcceleration(kMaxAcceleration)
          .withMotionMagicJerk(kMaxJerk));

}
