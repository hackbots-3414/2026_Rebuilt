package frc.robot.subsystems.climber;

//import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;

public class Climber extends SubsystemBase {
    private final ClimberIO io;
    private final ClimberIOInputs inputs = new ClimberIOInputs();
    private int climbLevel = 0;

    public Climber(ClimberIO io) {
      this.io = io;
      SmartDashboard.putData("Climber/Climb", climb());
    }
    
    @Override
    public void periodic() {
      io.updateInputs(inputs);
      SmartDashboard.putNumber("ClimbLevel", climbLevel);
    }
    
    public int climb(int climbLevel) {
      this.climbLevel = climbLevel;
      if (climbLevel == 0) {
        climbLevel = 1;
      } else if (climbLevel == 1) {
        System.out.println("Climber is already at max level!");
      }
      return climbLevel;
    }
   
    public Command climb () {
      climbLevel = 1;
      return Commands.sequence(
        runOnce(this::climb)
      );
    }
}
