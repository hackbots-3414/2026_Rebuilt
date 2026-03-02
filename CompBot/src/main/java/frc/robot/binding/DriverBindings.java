package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.AimPrep;
import frc.robot.commands.ResetForwards;
import frc.robot.commands.RunIntake;
import frc.robot.superstructure.Superstructure;

public class DriverBindings implements Binder {
  private final CommandPS5Controller controller;

  private final DoubleSupplier vx, vy, vrot;

  public DriverBindings() {
    controller = new CommandPS5Controller(Driver.kDriveControllerPort);
    vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
    vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
    vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);
  }

  public void bind(Superstructure superstructure) {
    superstructure.bindDrive(vx, vy, vrot);

    controller.button(3).toggleOnTrue(superstructure.build(new AimPrep()));
    controller.button(4).whileTrue(superstructure.build(new RunIntake()));
    controller.button(1).onTrue(superstructure.build(new ResetForwards()));

    // controller.cross().onTrue(superstructure.build(new RunClimb(ClimbPosition.Home)));
    // controller.triangle().onTrue(superstructure.build(new RunClimb(ClimbPosition.Ready)));
    // controller.circle().onTrue(superstructure.build(new RunClimb(ClimbPosition.Climbed)));

    // controller.L2().whileTrue(superstructure.build(new RunIndex()));
  }
}
