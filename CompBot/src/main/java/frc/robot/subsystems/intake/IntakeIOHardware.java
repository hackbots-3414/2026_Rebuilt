package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Rotations;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANrange;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Current;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants;
import frc.robot.util.StatusSignalUtil;

public class IntakeIOHardware implements IntakeIO {

  private final TalonFX intakeMotor;
  private final TalonFX deployMotor;
  private final CANrange canrange;

  private final TorqueCurrentFOC intakeControl = new TorqueCurrentFOC(0);
  private final DynamicMotionMagicTorqueCurrentFOC deployControl =
      new DynamicMotionMagicTorqueCurrentFOC(
          Rotations.zero(),
          DeployConstants.kMaxVelocity,
          DeployConstants.kMaxAcceleration);

  private Current lastCurrent = Amps.zero();

  public IntakeIOHardware() {
    intakeMotor = new TalonFX(IntakeConstants.kIntakeMotorId);
    intakeMotor.getConfigurator().apply(IntakeConstants.kIntakeMotorConfig);
    deployMotor = new TalonFX(DeployConstants.kDeployMotorId);
    deployMotor.getConfigurator().apply(DeployConstants.kDeployMotorConfig);

    canrange = new CANrange(IntakeConstants.kcanrangeID);
    canrange.getConfigurator().apply(IntakeConstants.kCANrangeConfig);

    StatusSignalUtil.registerRioSignals(
        intakeMotor.getSupplyCurrent(false),
        intakeMotor.getTorqueCurrent(false),
        intakeMotor.getStatorCurrent(false),
        intakeMotor.getMotorVoltage(false),
        intakeMotor.getDeviceTemp(false),
        intakeMotor.getVelocity(false),

        deployMotor.getSupplyCurrent(false),
        deployMotor.getTorqueCurrent(false),
        deployMotor.getStatorCurrent(false),
        deployMotor.getMotorVoltage(false),
        deployMotor.getDeviceTemp(false),
        deployMotor.getVelocity(false),
        deployMotor.getPosition(false),

        canrange.getDistance(false),
        canrange.getIsDetected(false));
  }

  public void updateInputs(IntakeIOInputs inputs) {
    inputs.intakeMotorConnected = BaseStatusSignal.isAllGood(
        intakeMotor.getSupplyCurrent(false),
        intakeMotor.getTorqueCurrent(false),
        intakeMotor.getStatorCurrent(false),
        intakeMotor.getMotorVoltage(false),
        intakeMotor.getDeviceTemp(false),
        intakeMotor.getVelocity(false));

    inputs.intakeSupplyCurrent = intakeMotor.getSupplyCurrent(false).getValue();
    inputs.intakeTorqueCurrent = intakeMotor.getTorqueCurrent(false).getValue();
    inputs.intakeStatorCurrent = intakeMotor.getStatorCurrent(false).getValue();
    inputs.intakeVoltage = intakeMotor.getMotorVoltage(false).getValue();
    inputs.intakeTemperature = intakeMotor.getDeviceTemp(false).getValue();
    inputs.intakeVelocity = intakeMotor.getVelocity(false).getValue();

    inputs.deployMotorConnected = BaseStatusSignal.isAllGood(
        deployMotor.getSupplyCurrent(false),
        deployMotor.getTorqueCurrent(false),
        deployMotor.getStatorCurrent(false),
        deployMotor.getMotorVoltage(false),
        deployMotor.getDeviceTemp(false),
        deployMotor.getVelocity(false),
        deployMotor.getPosition(false));

    inputs.deploySupplyCurrent = deployMotor.getSupplyCurrent(false).getValue();
    inputs.deployTorqueCurrent = deployMotor.getTorqueCurrent(false).getValue();
    inputs.deployStatorCurrent = deployMotor.getStatorCurrent(false).getValue();
    inputs.deployVoltage = deployMotor.getMotorVoltage(false).getValue();
    inputs.deployTemperature = deployMotor.getDeviceTemp(false).getValue();
    inputs.deployVelocity = deployMotor.getVelocity(false).getValue();
    inputs.deployPosition = deployMotor.getPosition(false).getValue();

    inputs.canrangeDetected = BaseStatusSignal.isAllGood(
        canrange.getDistance(false),
        canrange.getIsDetected(false));

    inputs.canrangeDistance = canrange.getDistance(false).getValue();
    inputs.canrangeDetected = canrange.getIsDetected(false).getValue();
  }

  public void setIntakeCurrent(Current current) {
    if (!current.equals(lastCurrent)) {
      intakeMotor.setControl(intakeControl.withOutput(current));
      lastCurrent = current;
    }
  }

  public void setDeployPosition(Angle position) {
    deployMotor.setControl(deployControl.withPosition(position));
  }
}
