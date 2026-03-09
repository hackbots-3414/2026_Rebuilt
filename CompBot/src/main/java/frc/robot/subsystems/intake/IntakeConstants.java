package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;

public class IntakeConstants {
  protected static final int kIntakeMotorId = 5;

  protected static final Voltage kIntakeVoltage = Volts.of(12.0);
  protected static final Voltage kEjectVoltage = Volts.of(-8);

  protected static final TalonFXConfiguration kIntakeMotorConfig = new TalonFXConfiguration()
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Coast)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(80)
          .withStatorCurrentLimit(120));

  protected static final Current kJamStatorThreshold = Amps.of(70);
  protected static final AngularVelocity kJamVelocityThreshold = RotationsPerSecond.of(0.3);

  public static final class DeployConstants {
    protected static final int kDeployMotorId = 6;
    protected static final int kCANcoderId = 29;

    protected static final TalonFXConfiguration kDeployMotorConfig = new TalonFXConfiguration()
        .withFeedback(new FeedbackConfigs()
            .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
            .withFeedbackRemoteSensorID(kCANcoderId))

        .withMotorOutput(new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Brake)
            .withInverted(InvertedValue.Clockwise_Positive))

        .withCurrentLimits(new CurrentLimitsConfigs()
            .withSupplyCurrentLimitEnable(true)
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(10)
            .withStatorCurrentLimit(20))

        .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
            .withForwardSoftLimitEnable(true)
            .withForwardSoftLimitThreshold(0.21)
            .withReverseSoftLimitEnable(true)
            .withReverseSoftLimitEnable(true))

        .withSlot0(new Slot0Configs()
            .withKA(0)
            .withKV(0)
            .withKS(0)
            .withKP(50)
            .withKI(0)
            .withKD(1)
            .withKG(-0.5));

    protected static final CANcoderConfiguration kCANcoderConfig = new CANcoderConfiguration()
        .withMagnetSensor(new MagnetSensorConfigs()
            .withSensorDirection(SensorDirectionValue.Clockwise_Positive)
            .withAbsoluteSensorDiscontinuityPoint(0.5)
            .withMagnetOffset(0.460205078125));

    protected static final AngularVelocity kMaxVelocity = RotationsPerSecond.of(0.4);
    protected static final AngularAcceleration kMaxAcceleration = RotationsPerSecondPerSecond.of(4);

    public static enum DeployPosition {
      Stow(Rotations.zero()),
      Agitate(Rotations.of(0.1)),
      Deployed(Rotations.of(0.21));

      protected final Angle position;

      private DeployPosition(Angle position) {
        this.position = position;
      }
    }

    protected static final Angle kTolerance = Rotations.of(0.5);
  }
}
