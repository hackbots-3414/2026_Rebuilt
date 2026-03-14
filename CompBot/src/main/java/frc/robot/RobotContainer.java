package frc.robot;

import java.nio.channels.Pipe;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.binding.AutogenBindings;
import frc.robot.binding.Binder;
import frc.robot.binding.DriverXboxBindings;
import frc.robot.binding.KeyboardBindings;
import frc.robot.binding.MultiBindings;
import frc.robot.binding.NamedCommandBindings;
import frc.robot.binding.OperatorPS5Bindings;
import frc.robot.binding.RobotBindings;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.localization.AprilTagVisionHandler;

public class RobotContainer {

  public final Superstructure superstructure;
  public final AprilTagVisionHandler aprilTagVisionHandler;

  public final Binder hidBinder = (Robot.isReal()) ? new MultiBindings(
    new DriverXboxBindings(),
    new OperatorPS5Bindings()
  ) : new KeyboardBindings();

  public final Binder robotBinder = new RobotBindings();
  public final Binder autogenBinder = new AutogenBindings();
  public final Binder ppBinder = new NamedCommandBindings();

  public RobotContainer() {
    superstructure = new Superstructure();
    robotBinder.bind(superstructure);
    ppBinder.bind(superstructure);
    hidBinder.bind(superstructure);
    autogenBinder.bind(superstructure);
    aprilTagVisionHandler = superstructure.createAprilTagVisionHandler();
    superstructure.createAutonChooser();

    // SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());
  }
}
