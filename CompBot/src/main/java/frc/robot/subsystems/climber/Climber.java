package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;

//import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.climber.ClimberConstants.ClimberPositions;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;

public class Climber extends SubsystemBase {
  private final ClimberIO io;
  private final ClimberIOInputs inputs = new ClimberIOInputs();

  public Climber(ClimberIO io) {
    this.io = io;
    SmartDashboard.putData("Climber/ClimbTo0", climb(ClimberPositions.NotClimbed));
    SmartDashboard.putData("Climber/ClimbTo1", climb(ClimberPositions.Level1));
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    SmartDashboard.putNumber("Climber/ClimbLevel", inputs.position.in(Degrees));
  }

  public Command climb(ClimberPositions climbLevel) {
    return Commands.sequence(
        this.runOnce(() -> io.setPosition(climbLevel.position)),
        Commands.waitUntil(ready(climbLevel)));
  }

  public Trigger ready(ClimberPositions climbLevel) {

    return new Trigger(() -> {
      return ClimberConstants.kTolerance.in(Radians) >= Math
          .abs(inputs.position.in(Radians) - climbLevel.position.in(Radians));
    });
  }
}
