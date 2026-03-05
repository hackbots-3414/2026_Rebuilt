package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.AgitateIntake;
import frc.robot.commands.AimPrep;
import frc.robot.commands.CommandBuilder;
import frc.robot.commands.ResetForwards;
import frc.robot.commands.RunIntake;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure;
import frc.robot.superstructure.Superstructure.Subsystems;

public class DriverBindings implements Binder {
  private final CommandPS5Controller controller;

  private final DoubleSupplier vx, vy, vrot;

  private final Trigger shoot;
  private final Trigger intake;
  private final Trigger resetPerspective;
  private final Trigger returnHome;

  public DriverBindings() {
    controller = new CommandPS5Controller(Driver.kDriveControllerPort);
    vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
    vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
    vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);

    shoot = controller.button(3);
    intake = controller.button(4);
    resetPerspective = controller.button(1);
    returnHome = controller.button(2);
  }

  public void bind(Superstructure superstructure) {
    superstructure.bindDrive(vx, vy, vrot);

    shoot.toggleOnTrue(superstructure.build(new AimPrep()));
    intake.whileTrue(superstructure.build(new RunIntake()));
    resetPerspective.onTrue(superstructure.build(new ResetForwards()));
    returnHome.whileTrue(superstructure.build(new CommandBuilder() {
      public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.drivetrain().returnToMemory();
      }
    }));

    superstructure.state.shouldAgitate().and(intake.negate()).whileTrue(superstructure.build(new AgitateIntake()));
  }
}
