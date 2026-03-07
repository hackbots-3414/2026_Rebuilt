package frc.robot.aiming;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.aiming.AimParams.AimStatus;
import frc.robot.aiming.AimParams.SpeedControl;

public class TuneAim implements AimStrategy {
    public AimParams update(Pose3d target, Pose3d shooter, Translation2d velocity) {
        AimParams params = new AimParams(AimStatus.Possible);

        double pitch = SmartDashboard.getNumber("Aim Tuning/Pitch", 60.0);
        double output = SmartDashboard.getNumber("Aim Tuning/Output", 0);

        SmartDashboard.putNumber("Aim Tuning/Pitch", pitch);
        SmartDashboard.putNumber("Aim Tuning/Output", output);

        params.pitch = Rotation2d.fromDegrees(pitch);
        params.output = output;
        params.control = SpeedControl.MechanismControl;

        return params;
    }
}
