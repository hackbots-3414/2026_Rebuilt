// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Volts;
import com.ctre.phoenix6.HootAutoReplay;
import com.ctre.phoenix6.HootEpilogueBackend;
import com.ctre.phoenix6.Utils;

import edu.wpi.first.epilogue.Epilogue;
import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.epilogue.logging.EpilogueBackend;
import edu.wpi.first.epilogue.logging.NTEpilogueBackend;
import edu.wpi.first.epilogue.logging.errors.ErrorHandler;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.OnboardLogger;
import frc.robot.util.StatusSignalUtil;

@Logged
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private boolean hasStartedVision;

  private final RobotContainer robotContainer;

  private final OnboardLogger oLogger;

  /* log and replay timestamp and joystick data */
  private final HootAutoReplay timeAndJoystickReplay = new HootAutoReplay()
      .withTimestampReplay()
      .withJoystickReplay();

  public Robot() {
    robotContainer = new RobotContainer();

    if (isSimulation()) {
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    DataLogManager.start();

    Epilogue.configure(config -> {
      // Log to both the Phoenix 6 SignalLogger
      // and NT4 backends
      config.backend = EpilogueBackend.multi(
          new HootEpilogueBackend(),
          new NTEpilogueBackend(NetworkTableInstance.getDefault()));

      if (Utils.isSimulation()) {
        // Re-throw any errors that occur in simulation
        config.errorHandler = ErrorHandler.crashOnError();
      }
    });

    Epilogue.bind(this);

    DriverStation.startDataLog(DataLogManager.getLog());

    oLogger = new OnboardLogger("Robot");
    oLogger.registerMeasurment("Battery Voltage", RobotController::getMeasureBatteryVoltage, Volts);
  }

  @Override
  public void robotPeriodic() {
    FieldManager.getInstance().clearFuel();

    StatusSignalUtil.refreshAll();
    CommandScheduler.getInstance().run();
    robotContainer.superstructure.periodic();

    FieldManager.getInstance().drawFuel();

    OnboardLogger.logAll();
    timeAndJoystickReplay.update();
  }

  @Override
  public void disabledInit() {
    if (!hasStartedVision) {
      robotContainer.aprilTagVisionHandler.startThread();
      hasStartedVision = true;
    }
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void disabledExit() {
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = robotContainer.getAutonomousCommand();

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
}
