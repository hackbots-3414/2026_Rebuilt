// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.HootAutoReplay;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.Autogen;
import frc.robot.util.StatusSignalUtil;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private boolean hasStartedVision;

  private final RobotContainer m_robotContainer;

  /* log and replay timestamp and joystick data */
  private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
      .withTimestampReplay()
      .withJoystickReplay();

  public Robot() {
    m_robotContainer = new RobotContainer();

    if (isSimulation()) {
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    // Test some autogen stuff
    Autogen.registerCommand("A", Commands.print("A ran!"));
    Autogen.registerCommand("B", Commands.print("B ran!"));
    Autogen.registerCommand("C", Commands.print("C ran!"));
  }

  @Override
  public void robotPeriodic() {
    FieldManager.getInstance().clearFuel();
    m_timeAndJoystickReplay.update();
    StatusSignalUtil.refreshAll();
    CommandScheduler.getInstance().run();
    FieldManager.getInstance().drawFuel();
  }

  @Override
  public void disabledInit() {
    if (!hasStartedVision) {
      m_robotContainer.aprilTagVisionHandler.startThread();
      hasStartedVision = true;
    }
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    // m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    m_autonomousCommand = Autogen.loadFromFile("myauto.autogen");

    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().cancel(m_autonomousCommand);
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}
}
