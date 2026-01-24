package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;

public interface TurretIO {
  
  public class TurretIOInputs {
    public boolean turretMotorConnected = true;
    public Voltage turretVoltage = Volts.zero();
    public Current turretCurrent = Amps.zero();
    public Temperature turretTemp = Celsius.zero();
    public AngularVelocity turretVelocityRPS = RotationsPerSecond.zero();
    public Angle reference = Radians.zero();
    public Angle position = Radians.zero();
  }

  public void updateInputs(TurretIOInputs inputs);

  public void setPosition(Rotation2d position);

  public void calibrate();
}
