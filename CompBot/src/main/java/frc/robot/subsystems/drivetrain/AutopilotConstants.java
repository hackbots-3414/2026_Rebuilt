package frc.robot.subsystems.drivetrain;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import com.therekrab.autopilot.APConstraints;
import com.therekrab.autopilot.APProfile;
import com.therekrab.autopilot.Autopilot;

public class AutopilotConstants {
    private static final APConstraints kConstraints = new APConstraints()
        .withVelocity(2.0)
        .withAcceleration(10.0)
        .withJerk(8.0);

    private static final APProfile kProfile = new APProfile(kConstraints)
        .withBeelineRadius(Inches.of(5))
        .withErrorTheta(Degrees.of(3))
        .withErrorXY(Inches.of(2));

    public static final Autopilot kDefaultAutopilot = new Autopilot(kProfile);

    private static final APProfile kLooseProfile = new APProfile(kConstraints)
        .withBeelineRadius(Meters.of(0.25))
        .withErrorTheta(Degrees.of(30))
        .withErrorXY(Inches.of(4));

    public static final Autopilot kLooseAutopilot = new Autopilot(kLooseProfile);

    public static final class HeadingGains {
        protected static final double kP = 4.0;
        protected static final double kI = 0.0;
        protected static final double kD = 0.0;
    }
}
