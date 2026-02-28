package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Volts;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants;
import frc.robot.util.StatusSignalUtil;

public class IntakeIOHardware implements IntakeIO {

  private final TalonFX intakeMotor;
  private final TalonFX deployMotor;

  private final VoltageOut intakeControl = new VoltageOut(0).withEnableFOC(true);
  private final DynamicMotionMagicTorqueCurrentFOC deployControl =
      new DynamicMotionMagicTorqueCurrentFOC(
          Rotations.zero(),
          DeployConstants.kMaxVelocity,
          DeployConstants.kMaxAcceleration);

  private Voltage lastVoltage = Volts.zero();

  public IntakeIOHardware() {
    intakeMotor = new TalonFX(IntakeConstants.kIntakeMotorId);
    intakeMotor.getConfigurator().apply(IntakeConstants.kIntakeMotorConfig);
    deployMotor = new TalonFX(DeployConstants.kDeployMotorId, StatusSignalUtil.canbus);
    deployMotor.getConfigurator().apply(DeployConstants.kDeployMotorConfig);

    StatusSignalUtil.registerRioSignals(
        intakeMotor.getSupplyCurrent(false),
        intakeMotor.getTorqueCurrent(false),
        intakeMotor.getStatorCurrent(false),
        intakeMotor.getMotorVoltage(false),
        intakeMotor.getDeviceTemp(false),
        intakeMotor.getVelocity(false));

    StatusSignalUtil.registerCANivoreSignals(
        deployMotor.getSupplyCurrent(false),
        deployMotor.getTorqueCurrent(false),
        deployMotor.getStatorCurrent(false),
        deployMotor.getMotorVoltage(false),
        deployMotor.getDeviceTemp(false),
        deployMotor.getVelocity(false),
        deployMotor.getPosition(false)
    );

    deployMotor.setPosition(0.0);
    SmartDashboard.putData("Intake/Set Zero", Commands.run(() -> deployMotor.setPosition(0)));
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
  }

  public void setIntakeVoltage(Voltage voltage) {
    if (!voltage.equals(lastVoltage)) {
      intakeMotor.setControl(intakeControl.withOutput(voltage.baseUnitMagnitude()));
      lastVoltage = voltage;
    }
  }

  public void setDeployPosition(Angle position) {
    deployMotor.setControl(deployControl.withPosition(position));
  }
}
