package frc.robot.subsystems.turret;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;

public class TurretIODisabled implements TurretIO {
  public void calibrate() {}

  public void setPosition(Angle angle) {}

  public void setVelocity(AngularVelocity velocity) {}

  public void updateInputs(TurretIOInputs inputs) {}
}
