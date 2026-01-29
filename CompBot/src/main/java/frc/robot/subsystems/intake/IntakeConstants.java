package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Voltage;

public class IntakeConstants {
    protected static final int kMotorRollerID = 60;
    protected static final int kMotorDropID = 61;
    protected static final int kcanrangeID = 25;

    protected static final double kSupplyCurrentLimit = 40.0;
    protected static final double kStatorCurrentLimit = 125.0;

    protected static final Voltage kIntakeVoltage =  Volts.of(5.0);
    protected static final Voltage kEjectVoltage = Volts.of(-5);

    protected static final Voltage kDropVoltage = Volts.of(5.0);
    protected static final Voltage kRaiseVoltage = Volts.of(-5.0);
    

    protected static final double canrangeMax = 6;




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
