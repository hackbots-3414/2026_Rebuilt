package frc.robot.subsystems.climber;

public interface ClimberIO {
    void updateInputs(ClimberIOInputs inputs);

    class ClimberIOInputs { 
        public boolean motorConnected = true;
        public double current = 0.0;
        public double torque = 0.0;
        public double voltage = 0.0;
        public double temperature = 0.0;
        public double stator = 0.0;
    }
    void setVoltage(double voltage);
}

