package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Voltage;

public final class IndexerConstants {

  public static final class FeederConstants {
    protected static final int kMotorID = 2;

    protected static final double kSupplyCurrentLimit = 40.0;
    protected static final double kStatorCurrentLimit = 125.0;

    protected static final Voltage kIndexVoltage = Volts.of(0.2);
    protected static final Voltage kEjectVoltage = Volts.of(-0.2);

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

}
