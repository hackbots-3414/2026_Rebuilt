
package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
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

  /**
   * Returns whether a jam may be hypothetically occuring. This requies three things to both be
   * true: Firstly, the intake motor must be drawing a lot of current. Secondly, there must be a
   * gamepiece detected in the intake system. Thirdly, the intake motors must not be moving very
   * quickly. If all these conditions are true, then a jam is possible.
   */
  public Trigger detectJam() {
    return new Trigger(
        () -> (inputs.intakeStatorCurrent.in(Amps) > IntakeConstants.kJamStatorThreshold.in(Amps))
            && inputs.canrangeDetected
            && inputs.intakeVelocity.baseUnitMagnitude() < IntakeConstants.kJamVelocityThreshold
                .baseUnitMagnitude());
  }
}
