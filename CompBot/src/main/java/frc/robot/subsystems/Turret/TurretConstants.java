package frc.robot.subsystems.Turret;

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

public class TurretConstants {

    protected static final int turretMotorID = 48;
    protected static final int kGear1CANcoderID = 49;
    protected static final int kGear2CANcoderID = 50;

    protected static final double kSupplyCurrentLimit = 100;

    protected static double kTurretHomePos = 0.5;

    //Find actual values
    protected static final double kGearRatio = 38.46;
    protected static final double kMaxSpeed = 32;
    protected static final double kMaxAcceleration = 48;
    protected static final double kMaxJerk = 480;

    //Find actual values
    protected static final double kTolerance = 5;

    protected static final double kGear1CANcoderOffset = -0.352051;
    protected static final double kGear2CANcoderOffset = -0.531006;

    // CANcoder configurations
    protected static final CANcoderConfiguration kGear1CANcoderConfig = new CANcoderConfiguration()
        .withMagnetSensor(new MagnetSensorConfigs()
            .withAbsoluteSensorDiscontinuityPoint(1.0)
            .withMagnetOffset(kGear1CANcoderOffset));

    protected static final CANcoderConfiguration kGear2CANcoderConfig = new CANcoderConfiguration()
        .withMagnetSensor(new MagnetSensorConfigs()
            .withAbsoluteSensorDiscontinuityPoint(1.0)
            .withMagnetOffset(kGear2CANcoderOffset)
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
          .withKP(30)
          .withKI(0)
          .withKD(0)
          .withKS(0.125)
          .withKV(0)
          .withKA(0))

      .withMotionMagic(new MotionMagicConfigs()
          .withMotionMagicCruiseVelocity(kMaxSpeed)
          .withMotionMagicAcceleration(kMaxAcceleration)
          .withMotionMagicJerk(kMaxJerk));

    public static class TurretCRTConstants {
        public static final double kG12 = 12.0 / 100.0;
        public static final double kG26 = (12.0 * 26.0) / (100.0 * 28.0);

        public static final long kAlpha = 312;
    }
    
}
