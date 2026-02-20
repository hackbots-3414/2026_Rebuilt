package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.binding.AutogenBindings;
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
  public final Binder autogenBinder = new AutogenBindings();

  public RobotContainer() {
    superstructure = new Superstructure();
    driverBinder.bind(superstructure);
    robotBinder.bind(superstructure);
    autogenBinder.bind(superstructure);
    aprilTagVisionHandler = superstructure.createAprilTagVisionHandler();
    superstructure.createAutonChooser();

    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());
  }
}
