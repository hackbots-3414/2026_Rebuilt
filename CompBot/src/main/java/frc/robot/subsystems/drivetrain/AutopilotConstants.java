package frc.robot.subsystems.drivetrain;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;

import com.therekrab.autopilot.APConstraints;
import com.therekrab.autopilot.APProfile;
import com.therekrab.autopilot.Autopilot;

public class AutopilotConstants {
    private static final APConstraints kConstraints = new APConstraints()
        .withVelocity(2.0)
        .withAcceleration(10.0)
        .withJerk(2.0);
    private static final APProfile kProfile = new APProfile(kConstraints)
        .withBeelineRadius(Inches.of(5))
        .withErrorTheta(Degrees.of(3))
        .withErrorXY(Inches.of(1));
    public static final Autopilot kAutopilot = new Autopilot(kProfile);

    public static final class HeadingGains {
        protected static final double kP = 30.0;
        protected static final double kI = 0.0;
        protected static final double kD = 0.0;
    }
}
