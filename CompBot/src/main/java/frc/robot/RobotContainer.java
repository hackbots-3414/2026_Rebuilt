// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.binding.Binder;
import frc.robot.binding.DriverBindings;
import frc.robot.generated.TestBotTunerConstants;
import frc.robot.superstructure.Superstructure;

public class RobotContainer {
    public final Superstructure superstructure = new Superstructure(TestBotTunerConstants.createDrivetrain());

    public final Binder driver = new DriverBindings();

    public RobotContainer() {
        driver.bind(superstructure);
    }

    public Command getAutonomousCommand() {
        return Commands.none();
    }
}
