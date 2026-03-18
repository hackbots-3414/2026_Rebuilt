
package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants.DeployPosition;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;

public class Intake extends SubsystemBase {
  private final IntakeIO io;
  private final IntakeIOInputs inputs;

  private boolean intaking = false;

  private DeployPosition reference = DeployPosition.Stow;

  public Intake(IntakeIO io) {
    super();
    this.io = io;
    inputs = new IntakeIOInputs();
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  /**
   * Returns a command, which, while running, will run the intake in reverse.
   */
  public Command reverse() {
    return startEnd(
        () -> io.setIntakeVoltage(IntakeConstants.kEjectVoltage),
        () -> io.setIntakeVoltage(Volts.zero()));
  }

  public Command intakeAt(DeployPosition state) {
    return Commands.sequence(
        runOnce(() -> {
          reference = state;
          io.setDeployPosition(state.position);
          io.setIntakeVoltage(IntakeConstants.kIntakeVoltage);
        }),
        this.idle())
        .finallyDo(() -> {
          io.setIntakeVoltage(Volts.zero());
        });
  }

  public void setIntaking(boolean v) {
    intaking = v;
  }

  public Command go(DeployPosition state) {
    return Commands.sequence(
        runOnce(() -> {
          reference = state;
          io.setDeployPosition(state.position);
        }),
        Commands.waitUntil(this::deployAtPosition));
  }

  private boolean deployAtPosition() {
    return Math.abs(inputs.deployPosition.minus(reference.position).baseUnitMagnitude()) <= DeployConstants.kTolerance
        .baseUnitMagnitude();
  }

  public Command agitate() {
    return Commands.repeatingSequence(
        intakeAt(DeployPosition.Deployed).withTimeout(0.5),
        intakeAt(DeployPosition.Agitate).withTimeout(0.5));
  }

  public Trigger intaking() {
    return new Trigger(() -> intaking);
  }
}
