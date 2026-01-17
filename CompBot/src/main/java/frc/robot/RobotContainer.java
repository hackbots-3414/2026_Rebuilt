// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.DriveForward;
import frc.robot.generated.TestBotTunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class RobotContainer {

   private final CommandXboxController joystick = new CommandXboxController(0);

    public final Superstructure superstructure = new Superstructure(TestBotTunerConstants.createDrivetrain());

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        superstructure.bindDrive(joystick);
    }

    public Command getAutonomousCommand() {
        return superstructure.build(new DriveForward());
    }
}
