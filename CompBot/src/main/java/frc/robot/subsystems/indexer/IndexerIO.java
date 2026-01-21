package frc.robot.subsystems.indexer;

public interface IndexerIO {
    void updateInputs(IndexerIOInputs inputs);

    class IndexerIOInputs { 
        public boolean motorConnected = true;
        public double current = 0.0;
        public double torque = 0.0;
        public double voltage = 0.0;
        public double temperature = 0.0;
        public double stator = 0.0;
    }
    void setVoltage(double voltage);
}
