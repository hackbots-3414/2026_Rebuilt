package frc.robot.binding;

import java.util.function.DoubleSupplier;

import com.therekrab.autopilot.APTarget;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.DriveToPoint;
import frc.robot.commands.ResetOdometry;
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

        controller.button(1).onTrue(superstructure.build(new ResetOdometry(Pose2d.kZero)));
        controller.button(3).whileTrue(superstructure.build(new DriveToPoint(new APTarget(Pose2d.kZero).withEntryAngle(Rotation2d.kZero))));
    }
}
