package frc.robot.binding;

import com.therekrab.autopilot.APTarget;

import frc.autogen.Autogen;
import frc.robot.Constants.AutonConstants;
import frc.robot.commands.AimPrep;
import frc.robot.commands.DriveToPoint;
import frc.robot.commands.ResetOdometry;
import frc.robot.commands.RunClimb;
import frc.robot.commands.RunIntake;
import frc.robot.commands.ShootWhenReady;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.superstructure.Superstructure;
import frc.robot.util.FieldUtils;

public class AutogenBindings implements Binder {
    public void bind(Superstructure superstructure) {
        // Test some autogen stuff
        Autogen.registerCommand("Intake", superstructure.build(new RunIntake()));
        Autogen.registerCommand("Aim", superstructure.build(new AimPrep()));
        Autogen.registerCommand("Climb Ready", superstructure.build(new RunClimb(ClimbPosition.Ready)));
        Autogen.registerCommand("Climb", superstructure.build(new RunClimb(ClimbPosition.Climbed)));
        Autogen.registerCommand("Shoot", superstructure.build(new ShootWhenReady()));

        Autogen.registerStartingPose("left", AutonConstants.kLeftStart);
        Autogen.registerStartingPose("right", AutonConstants.kRightStart);
        Autogen.registerStartingPose("depot", AutonConstants.kLeftStart);

        Autogen.registerCommand("Seed Left", superstructure.build(new ResetOdometry(AutonConstants.kLeftStart, true)));
        Autogen.registerCommand("Seed Right", superstructure.build(new ResetOdometry(AutonConstants.kRightStart, true)));

        registerAPTarget("Cross Right", AutonConstants.kCrossRight, superstructure);
        registerAPTarget("Cross Right Closer", AutonConstants.kCrossRightCloser, superstructure);
        registerAPTarget("Cross Right Angle", AutonConstants.kCrossRightAngle, superstructure);

        registerAPTarget("Cross Left", AutonConstants.kCrossLeft, superstructure);
        registerAPTarget("Cross Left Closer", AutonConstants.kCrossLeftCloser, superstructure);
        registerAPTarget("Cross Left Angle", AutonConstants.kCrossLeftAngle, superstructure);
        
        registerAPTarget("Surf Left", AutonConstants.kSurfLeft, superstructure);
        registerAPTarget("Surf Left Second", AutonConstants.kSurfLeftSecond, superstructure);
        registerAPTarget("Surf Left Close",AutonConstants.kSurfLeftClose, superstructure);  

        registerAPTarget("Surf Right", AutonConstants.kSurfRight, superstructure);
        registerAPTarget("Surf Right Second", AutonConstants.kSurfRightSecond, superstructure);
        registerAPTarget("Surf Right Close",AutonConstants.kSurfRightClose, superstructure);

        registerAPTarget("Surf Up Right", AutonConstants.kSurfUpRight, superstructure);
        registerAPTarget("Surf Up Left", AutonConstants.kSurfUpLeft, superstructure);


        registerAPTarget("Return Left", AutonConstants.kReturnLeft, superstructure);
        registerAPTarget("Return Left Angle", AutonConstants.kReturnLeftAngle, superstructure);        
        registerAPTarget("Return Right", AutonConstants.kReturnRight, superstructure);
        registerAPTarget("Return Right Angle", AutonConstants.kReturnRightAngle, superstructure);

        registerAPTarget("Depot", AutonConstants.kDepot, superstructure);
        registerAPTarget("Tower", AutonConstants.kTower, superstructure);
        registerAPTarget("Outpost", AutonConstants.kOutpost, superstructure);
    }

    private void registerAPTarget(String name, APTarget target, Superstructure superstructure) {
        Autogen.registerCommand(name, superstructure.build(new DriveToPoint(() -> FieldUtils.targetFlip(target))));
    }
}
