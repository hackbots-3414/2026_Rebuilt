package frc.robot.subsystems.Turret;

public interface TurretIO {
  
  public class ElevatorIOInputs {
    public boolean turretMotorConnected = true;
    public double turretVoltage = 0.0;
    public double turretCurrent = 0.0;
    public double turretTemp = 0.0;
    public double turretVelocityRPS = 0.0;
    public double turretPosition = 0.0;
    public double position = 0.0;
    public double reference = 0.0;
  }

  void setPosition(double position);

  void setVoltage(double voltage);

  void disableLimits();
  void enableLimits();

  void calibrateZero();
}
