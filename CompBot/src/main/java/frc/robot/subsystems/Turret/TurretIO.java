package frc.robot.subsystems.Turret;

public interface TurretIO {
  
  public class TurretIOInputs {
    public boolean turretMotorConnected = true;
    public double turretVoltage = 0.0;
    public double turretCurrent = 0.0;
    public double turretTemp = 0.0;
    public double turretVelocityRPS = 0.0;
    public double turretPosition = 0.0;
    public double position = 0.0;
    public double reference = 0.0;
    public double gear1CANcoder = 0;
    public double gear2CANcoder = 0;
    public double turretCANcoder = 0;
  }

  void updateInputs(TurretIOInputs inputs);

  void setPosition(double position);

  void setVoltage(double voltage);

  void disableLimits();
  void enableLimits();

  void calibrateZero();
}
