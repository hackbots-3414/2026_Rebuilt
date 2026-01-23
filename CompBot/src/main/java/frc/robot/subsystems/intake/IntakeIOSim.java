package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;

import edu.wpi.first.units.measure.Current;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOInputs;

public class IntakeIOSim {
    private Current current = Amps.zero();

    public void updateInputs(IntakeIOInputs inputs) {
        inputs.torqueCurrent = current;
    }

    public void setCurrent(Current current) {
         this.current = current;
    }

}
