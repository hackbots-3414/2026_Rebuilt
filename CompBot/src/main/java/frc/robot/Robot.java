// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.ActivityCalculator;
import frc.robot.util.ActivityCalculator.HubStatus;
import frc.robot.util.OnboardLogger;
import frc.robot.util.StatusSignalUtil;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private boolean hasStartedVision;

  private final RobotContainer robotContainer;

  private final OnboardLogger oLogger;

  public Robot() {
    robotContainer = new RobotContainer();

    if (isSimulation()) {
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog());

    oLogger = new OnboardLogger("Robot");
    oLogger.registerMeasurement("Battery Voltage", RobotController::getMeasureBatteryVoltage, Volts);
    oLogger.registerString("Game Data", DriverStation::getGameSpecificMessage);
  }

  @Override
  public void robotPeriodic() {
    FieldManager.getInstance().clearFuel();

    robotContainer.superstructure.periodic();
    StatusSignalUtil.refreshAll();
    CommandScheduler.getInstance().run();

    FieldManager.getInstance().drawFuel();

    OnboardLogger.logAll();

    SmartDashboard.putNumber("DS/Match Time", DriverStation.getMatchTime());
    HubStatus hubStatus = ActivityCalculator.update();
    SmartDashboard.putString("DS/Active (Color)", hubStatus.color(ActivityCalculator.us()));
    SmartDashboard.putBoolean("DS/Active (Boolean)", ActivityCalculator.is(ActivityCalculator.us()));
    SmartDashboard.putNumber("DS/Hub Time", hubStatus.timeRemaining());
    SmartDashboard.putString("DS/Current", hubStatus.active().toString());
  }

  @Override
  public void disabledInit() {
    if (!hasStartedVision) {
      // robotContainer.aprilTagVisionHandler.startThread();
      hasStartedVision = true;
    }
  }

  @Override
  public void disabledPeriodic() {
    ActivityCalculator.initialize();
  }

  @Override
  public void disabledExit() {
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = robotContainer.superstructure.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void autonomousExit() {
  }

  @Override
  public void teleopInit() {
    ActivityCalculator.initialize();
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().cancel(m_autonomousCommand);
    }
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void teleopExit() {
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void testExit() {
  }

  @Override
  public void simulationPeriodic() {
  }

  public Command getM_autonomousCommand() {
    return m_autonomousCommand;
  }

  public void setM_autonomousCommand(Command m_autonomousCommand) {
    this.m_autonomousCommand = m_autonomousCommand;
  }

  public boolean isHasStartedVision() {
    return hasStartedVision;
  }

  public void setHasStartedVision(boolean hasStartedVision) {
    this.hasStartedVision = hasStartedVision;
  }

  public RobotContainer getRobotContainer() {
    return robotContainer;
  }

  public OnboardLogger getoLogger() {
    return oLogger;
  }
}
