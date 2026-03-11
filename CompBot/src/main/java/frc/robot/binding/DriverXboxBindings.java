package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.AgitateIntake;
import frc.robot.commands.DrivetrainAim;
import frc.robot.commands.ResetForwards;
import frc.robot.commands.RunIntake;
import frc.robot.subsystems.drivetrain.Drivetrain.TeleopDriveMode;
import frc.robot.superstructure.Superstructure;
import frc.robot.util.RumbleUtil;
import frc.robot.util.RumbleUtil.RumbleStrength;

public class DriverXboxBindings implements Binder {
  private final CommandXboxController controller;

  private final DoubleSupplier vx, vy, vrot;

  private final Trigger shoot, intake, resetPerspective, robotRelativeDrive;

  public DriverXboxBindings() {
    controller = new CommandXboxController(Driver.kDriveControllerPort);

    vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
    vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
    vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);

    shoot = controller.rightBumper();
    intake = controller.rightTrigger();
    resetPerspective = controller.leftBumper();
    robotRelativeDrive = controller.leftTrigger();
  }

  public void bind(Superstructure superstructure) {
    superstructure.bindDrive(vx, vy, vrot, () -> robotRelativeDrive.getAsBoolean() ? TeleopDriveMode.RobotRelative : TeleopDriveMode.FieldRelativeSpin);

    shoot.toggleOnTrue(superstructure.build(new DrivetrainAim()));
    intake.whileTrue(superstructure.build(new RunIntake()));
    resetPerspective.onTrue(superstructure.build(new ResetForwards()));

    shoot.and(superstructure.state.shootReady()).onTrue(RumbleUtil.alert(controller, RumbleStrength.Medium, 0.5, 0.5).withTimeout(3.0));

    superstructure.state.shouldAgitate().and(intake.negate()).whileTrue(superstructure.build(new AgitateIntake()));
  }
}
