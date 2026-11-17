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
        /** Demo robot */
        DemoBot,
    }

    private static final Alert MODE_ALERT = new Alert("You are running a robot without known hardware", AlertType.kInfo);

    private static Optional<RobotId> robotId = Optional.empty();

    public static RobotId id() {
        if (robotId.isEmpty()) {
            robotId = Optional.of(calculateRobotId());
        }
        return robotId.get();
    }

    private static final String DEMOBOT_BREADCRUMB_PATH = "/etc/demobot";

    private static final RobotId calculateRobotId() {
        return RobotId.DemoBot;
    }

    public static void processRobotId() {
        String message = switch (id()) {
            case DemoBot -> "Running DEMOBOT code! If you are running this for a different purpose, please delete the file at " + DEMOBOT_BREADCRUMB_PATH;
        };
        MODE_ALERT.setText(message);
        MODE_ALERT.set(true);
    }
}
