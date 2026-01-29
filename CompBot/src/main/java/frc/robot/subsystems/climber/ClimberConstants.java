package frc.robot.subsystems.climber;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public final class ClimberConstants {

  protected static final int kMotorID = 60;

  protected static final double kSupplyCurrentLimit = 40.0;
  protected static final double kStatorCurrentLimit = 125.0;

  protected static enum CLIMBERPOSITIONS_ENUM {
    LEVEL0,
    LEVEL1,
    LEVEL2,
    LEVEL3
  }

  protected static final double kLevelZeroPosition = 0.0;
  protected static final double kLevelOnePosition = 1.0;
  protected static final double kLevelTwoPosition = 2.0;
  protected static final double kLevelThreePosition = 3.0;

  protected static final double delta = 0.5;

  protected static final double kMaxSpeed = 32;
  protected static final double kMaxAcceleration = 48;
  protected static final double kMaxVelocity = 480;

  protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Brake)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(kSupplyCurrentLimit)
          .withStatorCurrentLimit(kStatorCurrentLimit));
}
