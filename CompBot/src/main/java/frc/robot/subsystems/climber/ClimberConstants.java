package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecondPerSecond;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;

public final class ClimberConstants {

  protected static final int kMotorID = 64;

  public static enum ClimbPosition {
    Home(Rotations.zero()),
    Ready(Rotations.of(0.6)),
    Climbed(Rotations.of(0.5));
    
    protected final Angle position;

    private ClimbPosition(Angle position) {
      this.position = position;
    }
  }

  protected static final Angle kTolerance = Radians.of(Math.PI / 4);

  protected static final AngularVelocity kVelocity = RotationsPerSecond.of(32);
  protected static final AngularAcceleration kAcceleration = RotationsPerSecondPerSecond.of(40);

  protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Brake)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(40.0)
          .withStatorCurrentLimit(125.0));
}
