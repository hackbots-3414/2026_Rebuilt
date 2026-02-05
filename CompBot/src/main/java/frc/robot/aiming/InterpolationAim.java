package frc.robot.aiming;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import java.util.List;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import frc.robot.superstructure.StateManager;

public class InterpolationAim extends EmpiricalAim {
  private final InterpolatingDoubleTreeMap pitchMap;
  private final InterpolatingDoubleTreeMap velocityMap;

  public InterpolationAim(AimConstraints constraints, List<AimMeasurement> measurements) {
    super(constraints);
    pitchMap = new InterpolatingDoubleTreeMap();
    velocityMap = new InterpolatingDoubleTreeMap();
    for (AimMeasurement measurement : measurements) {
      pitchMap.put(
          measurement.distance().in(Meters),
          measurement.pitch().getRadians());
      velocityMap.put(
          measurement.distance().in(Meters),
          measurement.velocity().in(MetersPerSecond));
    }
  }

  public AimParams predict(StateManager state) {
    Translation3d offset = state.aimTarget().getTranslation()
        .minus(state.turretPose().getTranslation());
    double xyDistance = offset.toTranslation2d().getNorm();
    AimParams params = new AimParams();
    params.velocity = MetersPerSecond.of(velocityMap.get(xyDistance));
    params.pitch = Rotation2d.fromRadians(pitchMap.get(xyDistance));
    params.yaw = Rotation2d.fromRadians(Math.atan2(offset.getY(), offset.getX()));
    return params;
  }

}
