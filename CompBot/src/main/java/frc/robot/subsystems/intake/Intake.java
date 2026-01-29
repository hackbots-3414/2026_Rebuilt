
package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;

public class Intake extends SubsystemBase {
  private final IntakeIO io;
  private final IntakeIOInputs inputs;

  private boolean hasFuel;

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
        () -> io.setCurrent(IntakeConstants.kIntakeCurrent),
        () -> io.setCurrent(Amps.zero()));
  }

  public Command reverse() {
    return startEnd(
        () -> io.setCurrent(IntakeConstants.kEjectCurrent),
        () -> io.setCurrent(Amps.zero()));
  }

  public Trigger detectJam() {
    return new Trigger(
        () -> (inputs.supplyCurrent.in(Amps) > IntakeConstants.kStatorCurrentLimit) && hasFuel);
  }
}
