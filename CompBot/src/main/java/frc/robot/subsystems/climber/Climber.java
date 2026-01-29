package frc.robot.subsystems.climber;

//import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.climber.ClimberConstants.CLIMBERPOSITIONS_ENUM;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;

public class Climber extends SubsystemBase {
  private final ClimberIO io;
  private final ClimberIOInputs inputs = new ClimberIOInputs();

  public Climber(ClimberIO io) {
    this.io = io;
    SmartDashboard.putData("Climber/ClimbTo0", climb(CLIMBERPOSITIONS_ENUM.LEVEL0));
    SmartDashboard.putData("Climber/ClimbTo1", climb(CLIMBERPOSITIONS_ENUM.LEVEL1));
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    SmartDashboard.putNumber("Climber/ClimbLevel", inputs.position);
  }
  
  public Command climb(CLIMBERPOSITIONS_ENUM climbLevel) {
    switch (climbLevel) {
      case LEVEL0:
        if (Math.abs(inputs.position - ClimberConstants.kLevelOnePosition) <= ClimberConstants.kDelta) {
          return Commands.sequence(
              this.runOnce(() -> io.climb(climbLevel)),
              Commands.waitUntil(ready(climbLevel)));
        }
      default:
        if (Math.abs(inputs.position - ClimberConstants.kLevelZeroPosition) <= ClimberConstants.kDelta) {
          return Commands.sequence(
              this.runOnce(() -> io.climb(climbLevel)),
              Commands.waitUntil(ready(climbLevel)));
        }
    }

    // If not in the right place to climb, do nothing
    return Commands.none();

  }

  private Trigger ready(CLIMBERPOSITIONS_ENUM climbLevel) {
    switch (climbLevel) {
      case LEVEL0:
        return new Trigger(() -> {
          return ClimberConstants.kDelta >= Math.abs(inputs.position - 0);
        });
      default:
        return new Trigger(() -> {
          return ClimberConstants.kDelta >= Math.abs(inputs.position - ClimberConstants.kLevelOnePosition);
        });
    }
  }
}
