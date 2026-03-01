
package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants;
import frc.robot.subsystems.intake.IntakeConstants.DeployConstants.DeployPosition;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;

public class Intake extends SubsystemBase {
  private final IntakeIO io;
  private final IntakeIOInputs inputs;

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

  public Command intake() {
    return startEnd(
        () -> io.setIntakeVoltage(IntakeConstants.kIntakeVoltage),
        () -> io.setIntakeVoltage(Volts.zero())
    );
  }

  /**
   * Returns a command, which, while running, will run the intake in reverse.
   */
  public Command reverse() {
    return startEnd(
        () -> io.setIntakeVoltage(IntakeConstants.kEjectVoltage),
        () -> io.setIntakeVoltage(Volts.zero()));
  }

  public Command intakeAndGo(DeployPosition state) {
    return Commands.sequence(
      runOnce(() -> {
          reference = state;
          io.setDeployPosition(state.position);
          io.setIntakeVoltage(IntakeConstants.kIntakeVoltage);
        }),
        Commands.waitUntil(this::deployAtPosition)
    );
  }

  public Command go(DeployPosition state) {
    return Commands.sequence(
        runOnce(() -> {
          reference = state;
          io.setDeployPosition(state.position);
        }),
        Commands.waitUntil(this::deployAtPosition)
    );
  }

  private boolean deployAtPosition() {
    return Math.abs(inputs.deployPosition.minus(reference.position).baseUnitMagnitude()) <= DeployConstants.kTolerance.baseUnitMagnitude();
  }
}
