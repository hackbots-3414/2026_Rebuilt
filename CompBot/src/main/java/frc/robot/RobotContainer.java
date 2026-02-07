// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.binding.Binder;
import frc.robot.binding.DriverBindings;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.localization.AprilTagVisionHandler;

public class RobotContainer {
  public final Superstructure superstructure;
  public final AprilTagVisionHandler aprilTagVisionHandler;

  public final Binder driver = new DriverBindings();

  public RobotContainer() {
    superstructure = new Superstructure();
    driver.bind(superstructure);
    aprilTagVisionHandler = superstructure.createAprilTagVisionHandler();

    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());
    
  }
  
  
  public Command getAutonomousCommand() {
    return Commands.none();
  }
}
