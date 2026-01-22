package frc.robot.binding;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.binding.BindingConstants.Driver;
import frc.robot.superstructure.Superstructure;

public class DriverBindings implements Binder {
    private final CommandXboxController controller;

    private final DoubleSupplier vx, vy, vrot;

    public DriverBindings() {
        controller = new CommandXboxController(Driver.kDriveControllerPort);
        vx = () -> controller.getRawAxis(Driver.kXAxis) * ((Driver.kFlipX) ? -1.0 : 1.0);
        vy = () -> controller.getRawAxis(Driver.kYAxis) * ((Driver.kFlipY) ? -1.0 : 1.0);
        vrot = () -> controller.getRawAxis(Driver.kRotAxis) * ((Driver.kFlipRot) ? -1.0 : 1.0);
    }

    public void bind(Superstructure superstructure) {
        superstructure.bindDrive(vx, vy, vrot);
    }
}
