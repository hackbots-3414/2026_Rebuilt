package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Turret.TurretIO.TurretIOInputs;

import frc.robot.Robot;

public class Turret extends SubsystemBase {    

    private final TurretIO io;
    private final TurretIOInputs inputs;

    public Turret() {
        super();
        if (Robot.isReal()) {
            io = new TurretIOHardware();
        } else {
            io = new TurretIOSim();
        }
        inputs = new TurretIOInputs();
    }


    /**
     * Keeps the turret pointed at the correct target
     * @param configuration Robot's pose relative to target 
     */
    private void track(Pose2d configuration) {
        //Get current locations of turret and robot

        double turretAngleRadians = inputs.turretPosition.magnitude();
        double robotAngleRadians = configuration.getRotation().getRadians();

        //Calculate the turret's goal position in radians
        double turretGoal = robotAngleRadians - turretAngleRadians;

        io.setPosition(Radians.of(turretGoal));

    }

    public Command track(Pose2d configuration, Subsystem turret) {
        return Commands.run(() -> track(configuration), turret);
    }


    public Command setPosition(Angle referenceAngle) {
        return Commands.sequence(
            runOnce(() -> io.setPosition(referenceAngle)),
            Commands.waitUntil(ready()));
    }

    /**
     * Go to zero from turret's current location - Aligns to robot's 0
     */
    public Command home() {
        return Commands.sequence(
            runOnce(() -> io.setPosition(Radians.zero())),
            Commands.waitUntil(ready()));
    }

    public Trigger ready() {
        return new Trigger(
            () -> (Math.abs(inputs.reference.magnitude()-inputs.turretPosition.magnitude()) < TurretConstants.kTolerance));
    }

}
