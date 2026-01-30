package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecondPerSecond;
import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FovParamsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.ProximityParamsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.ToFParamsConfigs;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.UpdateModeValue;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;

public class IntakeConstants {
  protected static final int kIntakeMotorId = 60;
  protected static final int kcanrangeID = 25;

  protected static final Current kIntakeCurrent = Amps.of(5.0);
  protected static final Current kEjectCurrent = Amps.of(-5);

  protected static final TalonFXConfiguration kIntakeMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Brake)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(80)
          .withStatorCurrentLimit(120));

  protected static final Current kJamStatorThreshold = Amps.of(70);

  protected static final CANrangeConfiguration kCANrangeConfig = new CANrangeConfiguration()
      .withFovParams(new FovParamsConfigs()
          .withFOVRangeX(6.5)
          .withFOVRangeY(6.5))
      .withProximityParams(new ProximityParamsConfigs()
          .withMinSignalStrengthForValidMeasurement(15015)
          .withProximityThreshold(0.1))
      .withToFParams(new ToFParamsConfigs()
          .withUpdateMode(UpdateModeValue.ShortRange100Hz));

  public static final class DeployConstants {
    protected static final int kDeployMotorId = 61;

    protected static final TalonFXConfiguration kDeployMotorConfig = new TalonFXConfiguration()
        .withMotorOutput(new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Brake)
            .withInverted(InvertedValue.Clockwise_Positive))

        .withCurrentLimits(new CurrentLimitsConfigs()
            .withSupplyCurrentLimitEnable(true)
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(80)
            .withStatorCurrentLimit(120))

        .withSlot0(new Slot0Configs()
            .withKA(0)
            .withKV(0)
            .withKS(0)
            .withKP(0)
            .withKI(0)
            .withKD(0));

    protected static final AngularVelocity kMaxVelocity = RotationsPerSecond.of(0.4);
    protected static final AngularAcceleration kMaxAcceleration = RotationsPerSecondPerSecond.of(4);
  }
}
