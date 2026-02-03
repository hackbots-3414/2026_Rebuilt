package frc.robot.aiming;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.LinearVelocity;
import frc.robot.superstructure.StateManager;

public class PolyRegAim extends ExperimentalAim {
  private static final int degree = 4;
  private static final int maxIterations = Integer.MAX_VALUE;

  private static final PolynomialCurveFitter fitter = PolynomialCurveFitter
      .create(degree)
      .withMaxIterations(maxIterations);

  private final double[] velocityCoeffs;
  private final double[] pitchCoeffs;

  public PolyRegAim(List<AimMeasurement> measurements) {
    List<WeightedObservedPoint> velocityData = new ArrayList<>(measurements.size());
    List<WeightedObservedPoint> pitchData = new ArrayList<>(measurements.size());
    for (AimMeasurement measurement : measurements) {
      velocityData.add(new WeightedObservedPoint(1,
          measurement.distance().in(Meters),
          measurement.velocity().in(MetersPerSecond)));
      pitchData.add(new WeightedObservedPoint(1,
          measurement.distance().in(Meters),
          measurement.pitch().getRadians()));
    }
    velocityCoeffs = fitter.fit(velocityData);
    pitchCoeffs = fitter.fit(pitchData);
  }

  public AimParams predict(StateManager state) {
    Translation3d offset = state.aimTarget().getTranslation()
        .minus(state.turretPose().getTranslation());
    double xyDistance = offset.toTranslation2d().getNorm();
    // Calculate angles and field-relative fuel velocity
    Rotation2d yaw = Rotation2d.fromRadians(Math.atan2(offset.getY(), offset.getX()));
    Rotation2d pitch = Rotation2d.fromRadians(sample(pitchCoeffs, xyDistance));
    LinearVelocity velocity = MetersPerSecond.of(sample(velocityCoeffs, xyDistance));
    // Apply calculations
    AimParams params = new AimParams();
    params.yaw = yaw;
    params.pitch = pitch;
    params.velocity = velocity;
    return params;
  }

  private double sample(double[] coeffs, double x) {
    if (coeffs.length != degree + 1) {
      return Double.NaN; // there was an error
    }
    double y = 0;
    for (int n = 0; n < degree; n++) {
      y += coeffs[n] * Math.pow(x, n);
    }
    return y;
  }
}
