package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Robot;

public class ShooterIOSim implements ShooterIO {
  private final DCMotorSim motor;

  public ShooterIOSim() {
    motor = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
  }

  public void updateInputs(ShooterIOInputs inputs) {
    motor.update(Robot.kDefaultPeriod);
    inputs.flywheelMotorConnected = true;
    inputs.flywheelSupplyCurrent = Amps.of(motor.getCurrentDrawAmps());
    inputs.flywheelVoltage = Volts.of(motor.getInputVoltage());
    inputs.flywheelVelocity = motor.getAngularVelocity();
  }

  public void setVelocity(AngularVelocity velocity) {
    motor.setAngularVelocity(velocity.baseUnitMagnitude());
  }
  public void setAngle(Angle angle) {
    motor.setAngle(angle.baseUnitMagnitude());
  }
}
