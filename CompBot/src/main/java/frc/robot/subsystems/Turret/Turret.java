package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Turret.TurretIO.TurretIOInputs;
import frc.robot.superstructure.StateManager;
import frc.robot.aiming.AimParams;

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
        SmartDashboard.putData("Turret/home", home());
    }


    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Position", inputs.turretPosition.in(Degrees));
    }

    /**
     * Keeps the turret pointed at the correct target
     */
    private void track(StateManager configuration, Supplier<AimParams> aimParams) {
        Rotation2d angle = configuration.robotPose().getRotation();
        Rotation2d newAngle = aimParams.get().yaw.minus(angle);
        setPosition(newAngle.getMeasure());
        
    }

    public Command track(StateManager configuration, Supplier<AimParams> aimParams, Subsystem turret) {
        return Commands.run(() -> track(configuration, aimParams), turret);
    }


    public Command setPosition(Angle referenceAngle) {
        return Commands.sequence(
            runOnce(() -> io.setPosition(referenceAngle)),
            Commands.waitUntil(ready()));
    }

    public Angle getPosition() {
        return inputs.turretPosition;
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
            () -> (Math.abs(inputs.reference.in(Radians)-inputs.turretPosition.in(Radians)) < TurretConstants.kTolerance));
    }

}
