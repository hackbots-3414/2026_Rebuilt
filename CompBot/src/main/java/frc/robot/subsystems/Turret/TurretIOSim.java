package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import edu.wpi.first.units.measure.Angle;

public class TurretIOSim implements TurretIO {
  private Angle position = Radians.zero();
  private Angle reference = Radians.zero();

  public void updateInputs(TurretIOInputs inputs) {
    position = position.times(0.9).plus(reference);
    inputs.turretPosition = position;
    inputs.reference = reference;
  }

  public void setPosition(Angle position) {
    reference = position; 
  }
  
  public void enableLimits() {}
  public void disableLimits() {}

  public void calibrateZero() {}

}