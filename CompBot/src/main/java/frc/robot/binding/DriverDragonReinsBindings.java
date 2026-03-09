package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.AgitateIntake;
import frc.robot.commands.AimPrep;
import frc.robot.commands.ResetForwards;
import frc.robot.commands.RunIntake;
import frc.robot.subsystems.drivetrain.Drivetrain.TeleopDriveMode;
import frc.robot.superstructure.Superstructure;

public class DriverDragonReinsBindings implements Binder {
  private final CommandXboxController controller;

  private final DoubleSupplier vx, vy, vrot;

  private final Trigger shoot, intake, resetPerspective, robotRelativeDrive;

  public DriverDragonReinsBindings() {
    controller = new CommandXboxController(Driver.kDriveControllerPort);
    vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
    vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
    vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);

    shoot = controller.button(3);
    intake = controller.button(4);
    resetPerspective = controller.button(1);
    robotRelativeDrive = controller.button(2);
  }

  public void bind(Superstructure superstructure) {
    superstructure.bindDrive(vx, vy, vrot, () -> robotRelativeDrive.getAsBoolean() ? TeleopDriveMode.RobotRelative : TeleopDriveMode.FieldRelativeSpin);

    shoot.toggleOnTrue(superstructure.build(new AimPrep()));
    intake.whileTrue(superstructure.build(new RunIntake()));
    resetPerspective.onTrue(superstructure.build(new ResetForwards()));

    superstructure.state.shouldAgitate().and(intake.negate()).whileTrue(superstructure.build(new AgitateIntake()));
  }
}
