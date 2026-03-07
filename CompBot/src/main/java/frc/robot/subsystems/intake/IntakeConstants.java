package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecondPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.FovParamsConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.ProximityParamsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.ToFParamsConfigs;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.ctre.phoenix6.signals.UpdateModeValue;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;

public class IntakeConstants {
  protected static final int kIntakeMotorId = 5;

  protected static final Voltage kIntakeVoltage = Volts.of(5.0);
  protected static final Voltage kEjectVoltage = Volts.of(-5);

  protected static final int kCANcoderId = 50; // FIXME Placeholder, replace with actual value

  protected static final TalonFXConfiguration kIntakeMotorConfig = new TalonFXConfiguration()
       .withFeedback(new FeedbackConfigs()
            .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
            .withFeedbackRemoteSensorID(kCANcoderId)
            .withSensorToMechanismRatio(1))
  
      .withMotorOutput(new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Coast)
          .withInverted(InvertedValue.Clockwise_Positive))

      .withCurrentLimits(new CurrentLimitsConfigs()
          .withSupplyCurrentLimitEnable(true)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimit(80)
          .withStatorCurrentLimit(120));

    protected static final CANcoderConfiguration kCANcoderConfig = new CANcoderConfiguration()
            .withMagnetSensor(new MagnetSensorConfigs()
                .withSensorDirection(SensorDirectionValue.Clockwise_Positive)
                .withAbsoluteSensorDiscontinuityPoint(1) // We'll never get to this point
                .withMagnetOffset(0)); // FIXME Replace with actual values

  protected static final Current kJamStatorThreshold = Amps.of(70);
  protected static final AngularVelocity kJamVelocityThreshold = RotationsPerSecond.of(0.3);

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
    protected static final int kDeployMotorId = 6;

    protected static final TalonFXConfiguration kDeployMotorConfig = new TalonFXConfiguration()
        .withMotorOutput(new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Brake)
            .withInverted(InvertedValue.Clockwise_Positive))

        .withCurrentLimits(new CurrentLimitsConfigs()
            .withSupplyCurrentLimitEnable(true)
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(20)
            .withStatorCurrentLimit(20))

        .withSlot0(new Slot0Configs()
            .withKA(0)
            .withKV(0)
            .withKS(0)
            .withKP(0)
            .withKI(0)
            .withKD(0));

    protected static final AngularVelocity kMaxVelocity = RotationsPerSecond.of(0.4);
    protected static final AngularAcceleration kMaxAcceleration = RotationsPerSecondPerSecond.of(4);

    public static enum DeployPosition {
      Stow(Rotations.zero()),
      Deployed(Rotations.of(1.0));

      protected final Angle position;
      private DeployPosition(Angle position) {
        this.position = position;
      }
    }

    protected static final Angle kTolerance = Rotations.of(0.02);
  }
}
