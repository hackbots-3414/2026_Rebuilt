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

        Autogen.registerCommand("Seed Left", superstructure.build(new ResetOdometry(AutonConstants.kLeftStart, true)));
        Autogen.registerCommand("Seed Right", superstructure.build(new ResetOdometry(AutonConstants.kRightStart, true)));

        registerAPTarget("Cross Left", AutonConstants.kCrossLeft, superstructure);
        registerAPTarget("Cross Right", AutonConstants.kCrossRight, superstructure);

        registerAPTarget("Surf Left", AutonConstants.kSurfLeft, superstructure);
        registerAPTarget("Surf Right", AutonConstants.kSurfRight, superstructure);

        registerAPTarget("Return Left", AutonConstants.kReturnLeft, superstructure);
        registerAPTarget("Return Right", AutonConstants.kReturnRight, superstructure);

        registerAPTarget("Depot", AutonConstants.kDepot, superstructure);
        registerAPTarget("Tower", AutonConstants.kTower, superstructure);
        registerAPTarget("Outpost", AutonConstants.kOutpost, superstructure);
    }

    private void registerAPTarget(String name, APTarget target, Superstructure superstructure) {
        Autogen.registerCommand(name, superstructure.build(new DriveToPoint(() -> FieldUtils.targetFlip(target))));
    }
}
