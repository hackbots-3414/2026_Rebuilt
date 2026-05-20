package frc.robot.util;

import java.io.File;
import java.util.Optional;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import frc.robot.Robot;

/** A class to identify the robot on which the current code is running. */
public class RobotIdentifier {

    /** The robot that this code is currently running on. */
    public enum RobotId {
        /** Competition robot (default) */
        CompBot,
        /** Demo robot */
        DemoBot,
        /** Test robot */
        TestBot,
        /** Simulation robot */
        SimBot;
    }

    private static final Alert MODE_ALERT = new Alert("You are running a robot without known hardware", AlertType.kInfo);

    private static Optional<RobotId> robotId = Optional.empty();

    public static RobotId id() {
        if (robotId.isEmpty()) {
            robotId = Optional.of(calculateRobotId());
        }
        return robotId.get();
    }

    private static final String TESTBOT_BREADCRUMB_PATH = "/etc/testbot";
    private static final String DEMOBOT_BREADCRUMB_PATH = "/etc/demobot";

    private static final RobotId calculateRobotId() {
        if (Robot.isSimulation()) {
            return RobotId.SimBot;
        }

        File breadcrumb = new File(TESTBOT_BREADCRUMB_PATH);
        if (breadcrumb.exists()) {
            return RobotId.TestBot;
        }

        breadcrumb = new File(DEMOBOT_BREADCRUMB_PATH);
        if (breadcrumb.exists()) {
            return RobotId.DemoBot;
        }

        return RobotId.CompBot;
    }

    public static void processRobotId() {
        String message = switch (id()) {
            case TestBot -> "Running TESTBOT code! If you are running this for a different purpose, please delete the file at " + TESTBOT_BREADCRUMB_PATH;
            case DemoBot -> "Running DEMOBOT code! If you are running this for a different purpose, please delete the file at " + DEMOBOT_BREADCRUMB_PATH;
            case SimBot -> "Currently in simulation!";
            case CompBot -> "Currently running competition code.";
        };
        MODE_ALERT.setText(message);
        MODE_ALERT.set(true);
    }
}
