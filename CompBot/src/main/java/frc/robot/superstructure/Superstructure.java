package frc.robot.superstructure;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.CommandBuilder;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.localization.AprilTagVisionHandler;

public class Superstructure {
    public record Subsystems(Drivetrain drivetrain) {}

    private final Subsystems subsystems;

    public final StateManager state;

    public Superstructure(Drivetrain drivetrain) {
        subsystems = new Subsystems(drivetrain);
        state = new StateManager(subsystems);
    }

    public void bindDrive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier vrot) {
        subsystems.drivetrain.setDefaultCommand(subsystems.drivetrain.teleopDrive(vx, vy, vrot));
    }

    /**
     * Builds a command from a command builder, and returns it. This also sets that command to
     * run as a proxied command, because it's helpful for autons.
     */
    public Command build(CommandBuilder builder) {
        return buildWithoutProxy(builder).asProxy();
    }

    /**
     * The same thing as <code>build()</code>, except this is NOT a proxied command. This should be
     * used for default commands, where the command needs to explicity list its subsystems. However,
     * other than that, there aren't many uses for this method, so <b>use with care!</b>.
     */
    public Command buildWithoutProxy(CommandBuilder builder) {
        return builder.build(subsystems, state)
            .withName(builder.getClass().getSimpleName());
    }

    public AprilTagVisionHandler createAprilTagVisionHandler() {
        return new AprilTagVisionHandler(this);
    }
}
