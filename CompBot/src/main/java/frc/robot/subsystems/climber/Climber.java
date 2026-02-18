package frc.robot.subsystems.climber;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;

//import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;
import frc.robot.util.OnboardLogger;

public class Climber extends SubsystemBase {
  private final ClimberIO io;
  private final ClimberIOInputs inputs = new ClimberIOInputs();

  private ClimbPosition reference = ClimbPosition.Home;

  public Climber(ClimberIO io) {
    this.io = io;
    OnboardLogger log = new OnboardLogger("Climber");
    log.registerString("State", () -> reference.toString());
    SmartDashboard.putData("Climber/Home", go(ClimbPosition.Home));
    SmartDashboard.putData("Climber/Ready", go(ClimbPosition.Ready));
    SmartDashboard.putData("Climber/Climb", go(ClimbPosition.Climbed));
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    SmartDashboard.putNumber("Climber/ClimbLevel", inputs.position.in(Degrees));
  }

  public Command go(ClimbPosition climbLevel) {
    return Commands.sequence(
        this.runOnce(() -> {
          io.setPosition(climbLevel.position);
          reference = climbLevel;
        }),
        Commands.waitUntil(at(climbLevel)));
  }

  public Trigger at(ClimbPosition climbLevel) {
    return new Trigger(() -> {
      return ClimberConstants.kTolerance.in(Radians) >= Math
          .abs(inputs.position.in(Radians) - climbLevel.position.in(Radians));
    });
  }
}
