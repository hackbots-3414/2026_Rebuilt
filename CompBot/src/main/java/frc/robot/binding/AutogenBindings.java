package frc.robot.binding;

import com.therekrab.autopilot.APTarget;
import com.therekrab.autopilot.Autopilot;

import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.Autogen;
import frc.robot.Constants.AutonConstants;
import frc.robot.commands.AimPrep;
import frc.robot.commands.DriveToPoint;
import frc.robot.commands.RunClimb;
import frc.robot.commands.RunIntake;
import frc.robot.commands.ShootWhenReady;
import frc.robot.subsystems.climber.ClimberConstants.ClimbPosition;
import frc.robot.subsystems.drivetrain.AutopilotConstants;
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

        Autogen.registerCommand("Seed Left", Commands.none());
        Autogen.registerCommand("Seed Right", Commands.none());

        registerAPTargetLoose("Cross Right", AutonConstants.kCrossRight, superstructure);
        registerAPTargetLoose("Cross Right Closer", AutonConstants.kCrossRightCloser, superstructure);
        registerAPTargetLoose("Cross Right Angle", AutonConstants.kCrossRightAngle, superstructure);
        
        registerAPTargetLoose("Cross Left", AutonConstants.kCrossLeft, superstructure);
        registerAPTargetLoose("Cross Left Closer", AutonConstants.kCrossLeftCloser, superstructure);
        registerAPTargetLoose("Cross Left Angle", AutonConstants.kCrossLeftAngle, superstructure);
        
        registerAPTargetLoose("Surf Left", AutonConstants.kSurfLeft, superstructure);
        registerAPTargetLoose("Surf Left Second", AutonConstants.kSurfLeftSecond, superstructure);
        registerAPTargetLoose("Surf Left Close",AutonConstants.kSurfLeftClose, superstructure);  

        registerAPTargetLoose("Surf Right", AutonConstants.kSurfRight, superstructure);
        registerAPTargetLoose("Surf Right Second", AutonConstants.kSurfRightSecond, superstructure);
        registerAPTargetLoose("Surf Right Close",AutonConstants.kSurfRightClose, superstructure);

        registerAPTargetLoose("Surf Up Right", AutonConstants.kSurfUpRight, superstructure);
        registerAPTargetLoose("Surf Up Left", AutonConstants.kSurfUpLeft, superstructure);

        registerAPTargetLoose("Return Left", AutonConstants.kReturnLeft, superstructure);
        registerAPTargetLoose("Return Left Angle", AutonConstants.kReturnLeftAngle, superstructure);        
        registerAPTargetLoose("Return Right", AutonConstants.kReturnRight, superstructure);
        registerAPTargetLoose("Return Right Angle", AutonConstants.kReturnRightAngle, superstructure);

        registerAPTarget("Depot", AutonConstants.kDepot, superstructure);
        registerAPTarget("Tower", AutonConstants.kTower, superstructure);
        registerAPTarget("Outpost", AutonConstants.kOutpost, superstructure);
    }

    private void registerAPTarget(String name, APTarget target, Superstructure superstructure, Autopilot autopilot) {
        Autogen.registerCommand(name, superstructure.build(new DriveToPoint(() -> FieldUtils.targetFlip(target), autopilot)));
    }

    private void registerAPTarget(String name, APTarget target, Superstructure superstructure) {
        registerAPTarget(name, target, superstructure, AutopilotConstants.kDefaultAutopilot);
    }

    private void registerAPTargetLoose(String name, APTarget target, Superstructure superstructure) {
        registerAPTarget(name, target, superstructure, AutopilotConstants.kLooseAutopilot);
    }
}
