package frc.robot.vision.tracking;

import java.util.List;
import java.util.Optional;

import org.photonvision.PhotonCamera;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.superstructure.Superstructure;
import frc.robot.vision.CameraConfig;
import frc.robot.vision.CameraIO;
import frc.robot.vision.CameraIOHardware;
import frc.robot.vision.localization.CameraIOAprilTagSim;

public class MultiInputTracker {
    Superstructure superstructure;
    List<SingleInputTracker> trackers;
    public MultiInputTracker(Superstructure superstructure) {
        this.superstructure = superstructure;
        for (CameraConfig config : Constants.CamConstants.configs) {
        CameraIO io;
            if (Robot.isSimulation()) {
        io = new CameraIOAprilTagSim(config, superstructure.state::robotPose);
      } else {
        io = new CameraIOHardware(config);
      }
        SingleInputTracker tracker = new SingleInputTracker(io);
        trackers.add(tracker);
        }
    }

   // Someone eventually has to handle if there aren't any trackers, but it's not gonna be us.
    public Optional<Transform3d> refresh() {
        SingleInputTracker bestTracker = null;
        for (SingleInputTracker tracker : trackers) {
            tracker.refresh();
            if (tracker.getConfidence() == null) {
                continue;
            }
            if (bestTracker == null) {
                bestTracker = tracker;
            }
            if (tracker.getConfidence().get() > bestTracker.getConfidence().get()) {
                bestTracker = tracker;
            }
        }
        if (bestTracker == null) {
            return null;
        }
        SmartDashboard.putNumber("Transform of most recent Tag:", bestTracker.getTransform().get().getTranslation().toTranslation2d().getDistance(superstructure.state.robotPose().getTranslation()));
        return bestTracker.getTransform();
    }
}
