package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Robot;
import frc.robot.subsystems.climber.ClimberConstants.CLIMBERPOSITIONS_ENUM;

public class ClimberIOSim implements ClimberIO {
  private final DCMotorSim motor;
  private double position = 0;

  public ClimberIOSim() {
    motor = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
  }

  public void updateInputs(ClimberIOInputs inputs) {
    motor.update(Robot.kDefaultPeriod);
    inputs.position = position;
    inputs.motorConnected = true;
    inputs.supplyCurrent = Amps.of(motor.getCurrentDrawAmps());
    inputs.voltage = Volts.of(motor.getInputVoltage());
  }

  public void setVoltage(Voltage voltage) {
    motor.setInputVoltage(voltage.baseUnitMagnitude());
  }

  public void climb(CLIMBERPOSITIONS_ENUM climbLevel) {
    switch(climbLevel) {
      case LEVEL0:
      position = ClimberConstants.kLevelZeroPosition;
        break;
      case LEVEL1:
      position = ClimberConstants.kLevelOnePosition;
        break;
      case LEVEL2:
      position = ClimberConstants.kLevelTwoPosition;
        break;
      case LEVEL3:
      position = ClimberConstants.kLevelThreePosition;
        break;
    }
  }
}
