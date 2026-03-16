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
        /** Test robot */
        TestBot,
        /** Simulation robot */
        SimBot;
    }

    private static final Alert MODE_ALERT = new Alert("You are running a robot without known hardware", AlertType.kError);

    private static Optional<RobotId> robotId = Optional.empty();

    public static RobotId id() {
        if (robotId.isEmpty()) {
            robotId = Optional.of(calculateRobotId());
        }
        return robotId.get();
    }

    private static final String TESTBOT_BREADCRUMB_PATH = "/etc/testbot";


    private static final RobotId calculateRobotId() {
        if (Robot.isSimulation()) {
            return RobotId.SimBot;
        }

        File breadcrumb = new File(TESTBOT_BREADCRUMB_PATH);
        return breadcrumb.exists() ? RobotId.TestBot : RobotId.CompBot;
    }

    public static void processRobotId() {
        if (id() == RobotId.TestBot) {
            MODE_ALERT.setText("Running TESTBOT code! If you are running this on a competition robot, please delete the file at " + TESTBOT_BREADCRUMB_PATH);
        } else {
            MODE_ALERT.setText("Running: " + id().toString());
        }
        MODE_ALERT.set(true);
    }
}
