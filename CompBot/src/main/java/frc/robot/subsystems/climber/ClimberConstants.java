package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Radian;
import static edu.wpi.first.units.Units.Radians;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Angle;

public final class ClimberConstants {

  protected static final int kMotorID = 60;

  protected static final double kSupplyCurrentLimit = 40.0;
  protected static final double kStatorCurrentLimit = 125.0;

  protected static enum ClimberPositions {
    NotClimbed(Radians.zero()),
    Level1(Radians.of(Math.PI));

    protected final Angle position;
    private ClimberPositions(Angle posiiton) {
      this.position = posiiton;
    }
  }

  protected static final Angle kUnclimbedPosition = Radians.zero();
  protected static final Angle kLevelOnePosition = Radians.of(Math.PI);

  protected static final Angle kError = Radians.of(Math.PI/4);

  protected static final double kCruiseVelocity = 32;
  protected static final double kAcceleration = 48;
  protected static final double kJerk = 480;

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
