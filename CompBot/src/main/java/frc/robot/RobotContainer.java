package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.autogen.Autogen;
import frc.robot.binding.Binder;
import frc.robot.binding.DriverBindings;
import frc.robot.binding.RobotBindings;
import frc.robot.commands.VisionTest;
import frc.robot.generated.TestBotTunerConstants;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.localization.AprilTagVisionHandler;

public class RobotContainer {

  public final Superstructure superstructure;
  public final AprilTagVisionHandler aprilTagVisionHandler;

  public final Binder driver = new DriverBindings();
  public final Binder robot = new RobotBindings();

  private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    superstructure = new Superstructure(TestBotTunerConstants.createDrivetrain());
    driver.bind(superstructure);
    robot.bind(superstructure);
    aprilTagVisionHandler = superstructure.createAprilTagVisionHandler();

    SmartDashboard.putData("VISION TEST", superstructure.build(new VisionTest()));
    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());

    autoChooser = Autogen.autoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
