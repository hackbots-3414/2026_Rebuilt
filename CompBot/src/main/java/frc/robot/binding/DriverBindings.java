package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.commands.AimTrack;
import frc.robot.commands.FuelShotSim;
import frc.robot.superstructure.Superstructure;

public class DriverBindings implements Binder {
    private final PS5Controller controller;

    private final DoubleSupplier vx, vy, vrot;

    public DriverBindings() {
        controller = new PS5Controller(Driver.kDriveControllerPort);
        vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
        vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
        vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);
    }

    public void bind(Superstructure superstructure) {
        superstructure.bindDrive(vx, vy, vrot);
   // Turret control
        // controller.button(1).toggleOnTrue(superstructure.build(new AimTrack()));
        // controller.button(2).whileTrue(superstructure.build(new AimTrack()));

        superstructure.state.shootReady().whileTrue(superstructure.build(new FuelShotSim()).repeatedly());
    }
}
