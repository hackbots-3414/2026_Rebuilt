package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;

public interface IndexerIO {
    void updateInputs(IndexerIOInputs inputs);

    class IndexerIOInputs { 
        public boolean motorConnected = true;
        public Current supplyCurrent = Amps.zero();
        public Current torqueCurrent = Amps.zero();
        public Current statorCurrent = Amps.zero();
        public Voltage voltage = Volts.zero();
        public Temperature temperature = Celsius.zero();
        public AngularVelocity velocity = RotationsPerSecond.zero();
    }

    void setVoltage(Voltage voltage);
}
