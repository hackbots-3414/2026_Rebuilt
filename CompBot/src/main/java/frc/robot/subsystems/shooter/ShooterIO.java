package frc.robot.subsystems.shooter;

public interface ShooterIO {
    void updateInputs (shooterIOInputs inputs);

    class shooterIOInputs {
        public boolean motorConnected = true;
        public double current = 0.0;
        public double torque = 0.0;
        public double voltage = 0.0;
        public double temperature = 0.0;
        public double stator = 0.0;
    }
    void setVoltage(double voltage);
}
