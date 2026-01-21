package frc.robot.subsystems.Turret;

public class TurretIOSim implements TurretIO {
  private double m_position = 0;
  private double m_reference = 0;

  public void updateInputs(TurretIOInputs inputs) {
    m_position = 0.9 * m_position + 0.1 * m_reference;
    inputs.position = m_position;
    inputs.reference = m_reference;
  }

  public void setPosition(double position) {
    m_reference = position; 
  }

  public void setVoltage(double voltage) {}
  
  public void enableLimits() {}
  public void disableLimits() {}

  public void calibrateZero() {}

}