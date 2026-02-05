package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Robot;

public class ShooterIOSim implements ShooterIO {
  private final DCMotorSim shooterMotor;
  private final DCMotorSim hoodMotor;

  public ShooterIOSim() {
    shooterMotor = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
    hoodMotor = new DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getKrakenX60Foc(1), 2, 10),
        DCMotor.getKrakenX60Foc(1),
        0.01,
        0.02);
  }

  public void updateInputs(ShooterIOInputs inputs) {
    shooterMotor.update(Robot.kDefaultPeriod);
    inputs.shooter1MotorConnected = true;
    inputs.shooter1StatorCurrent = Amps.of(shooterMotor.getCurrentDrawAmps());
    inputs.shooter1Voltage = Volts.of(shooterMotor.getInputVoltage());
    inputs.shooter1Velocity = shooterMotor.getAngularVelocity();

    hoodMotor.update(Robot.kDefaultPeriod);
    inputs.hoodMotorConnected = true;
    inputs.hoodSupplyCurrent = Amps.of(hoodMotor.getCurrentDrawAmps());
    inputs.hoodVoltage = Volts.of(hoodMotor.getInputVoltage());
    inputs.hoodVelocity = hoodMotor.getAngularVelocity();
    inputs.hoodAngle = hoodMotor.getAngularPosition();
  }

  public void setVelocity(AngularVelocity velocity) {
    shooterMotor.setAngularVelocity(velocity.in(RadiansPerSecond));
  }

  public void setAngle(Angle angle) {
    hoodMotor.setAngle(angle.in(Radians));
  }
}
