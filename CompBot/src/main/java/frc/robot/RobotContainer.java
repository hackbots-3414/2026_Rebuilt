package frc.robot;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.autogen.Autogen;
import frc.robot.binding.Binder;
import frc.robot.binding.DriverBindings;
import frc.robot.binding.RobotBindings;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.localization.AprilTagVisionHandler;

public class RobotContainer {

  public final Superstructure superstructure;
  public final AprilTagVisionHandler aprilTagVisionHandler;

  public final Binder driverBinder = new DriverBindings();
  public final Binder robotBinder = new RobotBindings();

  private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    superstructure = new Superstructure();
    driverBinder.bind(superstructure);
    robotBinder.bind(superstructure);
    aprilTagVisionHandler = superstructure.createAprilTagVisionHandler();

    autoChooser = Autogen.autoChooser();

    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());
  }
  
  
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
