package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurretIOSim implements TurretIO {
  private Angle position = Radians.zero();
  private Angle reference = Radians.zero();

  public void updateInputs(TurretIOInputs inputs) {
    position = position.times(0.9).plus(reference);
    inputs.position = position;
    inputs.reference = reference;
    SmartDashboard.putNumber("Turret Location (degrees)", position.in(Degrees));
    SmartDashboard.putBoolean("Turret/Successful Calibration?", false);
  }

  public void setPosition(Rotation2d position) {
    reference = position.getMeasure();
  }

  public boolean calibrate() {
    return SmartDashboard.getBoolean("Turret/Successful Calibration?", true);
  }

}

