package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Rotations;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Current;

public class IntakeIOSim implements IntakeIO {
  private Current current = Amps.zero();
  private Angle position = Rotations.zero();

  public void updateInputs(IntakeIOInputs inputs) {
    inputs.intakeTorqueCurrent = current;
    inputs.deployPosition = position;
  }

  public void setIntakeCurrent(Current current) {
    this.current = current;
  }

  public void setDeployPosition(Angle angle) {}
}
