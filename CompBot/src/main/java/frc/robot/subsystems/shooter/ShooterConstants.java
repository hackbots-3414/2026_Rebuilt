package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public final class ShooterConstants {
    protected static final int kMotorID = 60;

    protected static final double kSupplyCurrentLimit = 40.0;
    protected static final double kStatorCurrentLimit = 125.0;

    protected static final TalonFXConfiguration kMotorConfig = new TalonFXConfiguration()
        .withSlot0(new Slot0Configs()
            .withKA(0)
            .withKS(0)
            .withKV(0)
            
            .withKP(0)
            .withKI(0)
            .withKD(0))

        .withMotorOutput(new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Brake)
            .withInverted(InvertedValue.Clockwise_Positive))

        .withCurrentLimits(new CurrentLimitsConfigs()
            .withSupplyCurrentLimitEnable(true)
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(kSupplyCurrentLimit)
            .withStatorCurrentLimit(kStatorCurrentLimit));
}
