package frc.robot;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import java.util.List;

import com.therekrab.autopilot.APTarget;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Distance;
import frc.robot.aiming.AimConstraints;
import frc.robot.aiming.AimStrategy;
import frc.robot.aiming.PhysicsAim;
import frc.robot.aiming.ToFAim;
import frc.robot.aiming.TuneAim;
import frc.robot.subsystems.shooter.ShooterConstants;
import frc.robot.util.BoundingBox;
import frc.robot.util.RobotIdentifier;
import frc.robot.util.RobotIdentifier.RobotId;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.localization.LocalizationConstants;

public class Constants {
    // Checked the FIRST Game Manual and fixed the field dimensions.
    public static class FieldConstants {
        /** The longer side, corresponds to X values */
        public static final Distance kFieldLength = Meters.of(16.541);
        /** The shorter side, corresponds to Y values */
        public static final Distance kFieldWidth = Meters.of(8.069);
        /** The length of the alliance zone, corresponds to X axis */
        public static final Distance kAllianceZoneLength = Meters.of(3.8);

        public static final Pose3d kBlueHub = new Pose3d(4.632516, 4.011139, 1.83, Rotation3d.kZero);
        public static final List<Pose2d> kFeedTargets = List.of(
                new Pose2d(2.5, 2, Rotation2d.kZero),
                new Pose2d(2.5, 6.0, Rotation2d.kZero));

        public static final BoundingBox kBlueTower = new BoundingBox(new Pose2d(0, 3.25, Rotation2d.kZero),
                new Pose2d(1, 4.15, Rotation2d.kZero));
        public static final BoundingBox kNoFeedZone = new BoundingBox(new Pose2d(5.5, 3.5, Rotation2d.kZero),
                new Pose2d(6.5, 4.6, Rotation2d.kZero));
    }

    public static class AimConstants {
        private static final AimConstraints constraints = new AimConstraints(
                Rotation2d.fromDegrees(49.5), // Min pitch
                Rotation2d.fromDegrees(72.0), // Max pitch
                ShooterConstants.kMaxRotationalSpeed.in(RotationsPerSecond)); // max output
        public static final AimStrategy kScoringAim = (RobotIdentifier.id() == RobotId.DemoBot) ? new TuneAim()
                // : new ToFAim(ShooterConstants.scoringMeasurements, constraints);
                : new PhysicsAim(constraints, 2, 10);
        public static final AimStrategy kFeedingAim = (RobotIdentifier.id() == RobotId.DemoBot) ? new TuneAim()
                // : new ToFAim(ShooterConstants.feedingMeasurements, constraints);
                : new PhysicsAim(constraints, 2, 10);
    }
    public static class CamConstants {
        public static final List<CameraConfig> configs = List.of(
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam1",
            () -> new Transform3d(-0.207, -0.318, 0.473,
                new Rotation3d(Units.degreesToRadians(0.7), Units.degreesToRadians(-28.578),
                    Units.degreesToRadians(-67.63)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam2",
            () -> new Transform3d(0.221, -0.262, 0.724,
                new Rotation3d(0, Units.degreesToRadians(-30), Units.degreesToRadians(26.3)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam3",
            () -> new Transform3d(0.121, 0.271, 0.709,
                new Rotation3d(0, Units.degreesToRadians(-5.1), Units.degreesToRadians(141.7)))),
        LocalizationConstants.kRegularBaseCameraConfig.cameraCopy(
            "cam4",
            () -> new Transform3d(-0.315, 0.138, 0.438,
                new Rotation3d(Units.degreesToRadians(-6.5), Units.degreesToRadians(-29.9),
                    Units.degreesToRadians(-169)))));
    }
    public static class AutonConstants {
        public static final Pose2d kLeftStart = new Pose2d(3.3, 5.7, Rotation2d.fromDegrees(-45));
        public static final Pose2d kRightStart = new Pose2d(3.3, 2.3, Rotation2d.fromDegrees(45));

        public static final APTarget kCrossLeft = new APTarget(new Pose2d(7.5, 6.7, Rotation2d.fromDegrees(-45)))
                .withVelocity(2);
        public static final APTarget kCrossRight = new APTarget(new Pose2d(7.5, 1.3, Rotation2d.fromDegrees(45)))
                .withVelocity(2);

        public static final APTarget kCrossRightCloser = new APTarget(
                new Pose2d(5.825, 2.3, Rotation2d.fromDegrees(45)))
                .withVelocity(0.5);
        public static final APTarget kCrossRightAngle = new APTarget(new Pose2d(5.825, 3.7, Rotation2d.fromDegrees(45)))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(0.5);

        public static final APTarget kCrossLeftCloser = new APTarget(new Pose2d(5.67, 5.6, Rotation2d.fromDegrees(-45)))
                .withVelocity(0.5);
        public static final APTarget kCrossLeftAngle = new APTarget(new Pose2d(5.825, 3.7, Rotation2d.fromDegrees(-45)))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(0.5);

        public static final APTarget kReturnLeft = new APTarget(new Pose2d(2.5, 5.7, Rotation2d.fromDegrees(-45)))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(2);
        public static final APTarget kReturnRight = new APTarget(new Pose2d(2.5, 2.3, Rotation2d.fromDegrees(45)))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(2);

        public static final APTarget kReturnRightAngle = new APTarget(new Pose2d(3.6, 2.3, Rotation2d.kCW_90deg))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(2);
        public static final APTarget kReturnLeftAngle = new APTarget(new Pose2d(3.6, 5.7, Rotation2d.kCCW_90deg))
                .withEntryAngle(Rotation2d.k180deg)
                .withVelocity(2);

        public static final APTarget kSurfLeft = new APTarget(new Pose2d(7.5, 4.0, Rotation2d.kCW_90deg))
                .withVelocity(0.5);
        public static final APTarget kSurfRight = new APTarget(new Pose2d(7.5, 4.0, Rotation2d.kCCW_90deg))
                .withVelocity(0.5);
        public static final APTarget kSurfUpRight = new APTarget(new Pose2d(7.7, 4.3, Rotation2d.kCW_90deg))
                .withVelocity(0.5);
        public static final APTarget kSurfUpLeft = new APTarget(new Pose2d(7.7, 3.3, Rotation2d.kCCW_90deg))
                .withVelocity(0.5);

        public static final APTarget kSurfLeftSecond = new APTarget(new Pose2d(7.7, 2.9, Rotation2d.fromDegrees(45)))
                .withVelocity(0.75);
        public static final APTarget kSurfRightSecond = new APTarget(new Pose2d(7.5, 2.5, Rotation2d.fromDegrees(-45)))
                .withVelocity(0.75);

        public static final APTarget kSurfRightClose = new APTarget(new Pose2d(5.8, 5.4, Rotation2d.kCCW_90deg))
                .withVelocity(0.75);
        public static final APTarget kSurfLeftClose = new APTarget(new Pose2d(5.8, 2.5, Rotation2d.kCW_90deg))
                .withVelocity(0.75);

        public static final APTarget kTower = new APTarget(new Pose2d(1.3, 3.8, Rotation2d.k180deg))
                .withEntryAngle(Rotation2d.k180deg);
        public static final APTarget kDepot = new APTarget(new Pose2d(0.55, 5.9, Rotation2d.k180deg))
                .withEntryAngle(Rotation2d.k180deg);
        public static final APTarget kOutpost = new APTarget(new Pose2d(0.42, 0.68, Rotation2d.k180deg));
    }
}
