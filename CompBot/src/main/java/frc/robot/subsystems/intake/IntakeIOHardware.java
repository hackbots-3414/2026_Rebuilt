package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants;
import frc.robot.util.StatusSignalUtil;

public class IntakeIOHardware implements IntakeIO {

  private final TalonFX intakeMotor;
  private final TalonFX deployMotor;
  private final CANcoder deployCANcoder;

  private final VoltageOut intakeControl = new VoltageOut(0);
  private final PositionVoltage deployControl = new PositionVoltage(0);

  private Voltage lastVoltage = Volts.zero();

  public IntakeIOHardware() {
    intakeMotor = new TalonFX(IntakeConstants.kIntakeMotorId);
    intakeMotor.getConfigurator().apply(IntakeConstants.kIntakeMotorConfig);

    deployCANcoder = new CANcoder(DeployConstants.kCANcoderId, StatusSignalUtil.canivore);
    deployCANcoder.getConfigurator().apply(DeployConstants.kCANcoderConfig);

    deployMotor = new TalonFX(DeployConstants.kDeployMotorId, StatusSignalUtil.canivore);
    deployMotor.getConfigurator().apply(DeployConstants.kDeployMotorConfig);

    RobotModeTriggers.autonomous().onTrue(Commands.startEnd(() -> {
      intakeMotor.getConfigurator().apply(IntakeConstants.kIntakeMotorConfigDuringAuto);
    }, () -> {
      intakeMotor.getConfigurator().apply(IntakeConstants.kIntakeMotorConfig);
    }));


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
        deployMotor.getPosition(false),
        
        deployCANcoder.getPosition(false),
        deployCANcoder.getVelocity(false));

    deployMotor.setPosition(0.0);
    // SmartDashboard.putData("Intake/Set Zero", Commands.runOnce(() -> deployMotor.setPosition(0)).ignoringDisable(true));
    // SmartDashboard.putData("Intake/Set Deployed", Commands.runOnce(() -> deployMotor.setPosition(DeployPosition.Deployed.position)).ignoringDisable(true));
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

    inputs.deployCANcoderConnected = BaseStatusSignal.isAllGood(
      deployCANcoder.getPosition(false),
      deployCANcoder.getVelocity(false)
    );
    inputs.deployCANcoderPosition = deployCANcoder.getPosition(false).getValue();
    inputs.deployCANcoderVelocity = deployCANcoder.getVelocity(false).getValue();
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
