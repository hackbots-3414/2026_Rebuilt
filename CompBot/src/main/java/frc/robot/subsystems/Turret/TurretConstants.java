package frc.robot.subsystems.Turret;

import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.FovParamsConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.ProximityParamsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class TurretConstants {

    protected static final int turretMotorID = 11;

    //Find these actual values
    protected static final int kGear1CANcoderID = 0;
    protected static final int kGear2CANcoderID = 1;
    protected static final int kMotorCANcoderID = 2;

    //Find actual values
    protected static final int kGear1Size = 28;
    protected static final int kGear2Size = 26;
    protected static final int kTurretSize = 100;

    protected static final double kSupplyCurrentLimit = 100;

    protected static double kTurretHomePos = 0.5;

    //Find actual values
    protected static final double kGearRatio = 1.07;
    protected static final double kMaxSpeed = 32;
    protected static final double kMaxAcceleration = 48;
    protected static final double kMaxJerk = 480;

    //Find actual values
    protected static final double kTolerance = 5;

    protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Brake)
          .withInverted(InvertedValue.CounterClockwise_Positive))

      .withFeedback(new FeedbackConfigs()
          .withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor)
          .withSensorToMechanismRatio(kGearRatio))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withSupplyCurrentLimit(kSupplyCurrentLimit))

      .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
          .withForwardSoftLimitThreshold(0.99)
          .withForwardSoftLimitEnable(true)
          .withReverseSoftLimitThreshold(0.01)
          .withReverseSoftLimitEnable(false))

      .withSlot0(new Slot0Configs()
          .withGravityType(GravityTypeValue.Elevator_Static)
          .withKP(20)
          .withKI(0)
          .withKD(0)
          .withKS(0.125)
          .withKV(0)
          .withKA(0)
          .withKG(0.42))

      .withMotionMagic(new MotionMagicConfigs()
          .withMotionMagicCruiseVelocity(kMaxSpeed)
          .withMotionMagicAcceleration(kMaxAcceleration)
          .withMotionMagicJerk(kMaxJerk));

  protected static final CANrangeConfiguration kCANrangeConfig = new CANrangeConfiguration()
      .withFovParams(new FovParamsConfigs()
          .withFOVRangeX(6.75)
          .withFOVRangeY(6.75))
      .withProximityParams(new ProximityParamsConfigs()
          .withMinSignalStrengthForValidMeasurement(3500)
          .withProximityThreshold(0.13)
          .withProximityHysteresis(0));

    
}
