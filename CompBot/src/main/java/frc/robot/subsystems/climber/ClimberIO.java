package frc.robot.subsystems.climber;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;

import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;


public interface ClimberIO {
    void updateInputs(ClimberIOInputs inputs);

    class ClimberIOInputs { 
        public boolean motorConnected = true;
        public Current supplyCurrent = Amps.zero();
        public Current torqueCurrent = Amps.zero();
        public Current statorCurrent = Amps.zero();
        public Voltage voltage = Volts.zero();
        public Temperature temperature = Celsius.zero();
    }
    void setVoltage(Voltage voltage);
}

