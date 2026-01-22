package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.hardware.CANcoder;

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
    public Angle turretPosition = Radians.zero();
    public Angle reference = Radians.zero();
    public Angle motorPosition = Radians.zero();
  }

  void updateInputs(TurretIOInputs inputs);

  void setPosition(Angle position);

  void disableLimits();
  void enableLimits();

  void calibrateZero();
}
