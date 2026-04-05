package frc.robot.vision;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

/**
 * A class reprenting the trustworthi-ness of a camera
 * 
 * @param baseStdDevs The base standard deviation matrix for this camera's pose estimates. This is
 *        basically the "baseline" for how much we trust the camera.
 * @param latencyThreshold The minimum latency that we'll accept from this camera, in seconds
 * @param latencyMultiplier The additional standard deviations per ms of latency.
 * @param fieldXYMargin The maximum horrizontal distance to the edge of the field for which a pose
 *        estimate can be to be considered "valid".
 * @param fieldZMargin The maximum vertical distance above the field that a pose estimate can be to
 *        be considered "valid".
 * @param noisyDistance The average tag distance after which we begin to add extra standard devs.
 * @param distanceMultiplier The additional standard deviations per meter of average distance to the
 *        tags above the noisy distance.
 * @param distanceMax The maximum average target distance we'll even consider.
 * @param ambiguityThreshold The maximum ambiguity acceptable.
 * @param ambiguityMultiplier The additional standard deviations per unit of ambiguity the camera
 *        reads.
 * @param ambiguityShifter This value is added to the measured ambiguity after multiplying it by the
 *        ambiguity multiplier.
 * @param targetDivisor The per-tag divisor for standard deviations. More tags => lower std devs =>
 *        more trust.
 * @param differenceThreshold The minimum difference between a new estimated pose and the most
 *        recently reported pose.
 * @param differenceMultiplier The additional standard deviations per meter of distance between the
 *        new pose estimate and the most recently accepted robot pose.
 */
public record CameraTrustConfig(
    Matrix<N3, N1> baseStdDevs,
    double latencyThreshold,
    double latencyMultiplier,
    double fieldXYMargin,
    double fieldZMargin,
    double noisyDistance,
    double distanceMultiplier,
    double distanceMax,
    double ambiguityThreshold,
    double ambiguityMultiplier,
    double ambiguityShifter,
    double targetDivisor,
    double differenceThreshold,
    double differenceMultiplier) {

  /** Returns a deep copy of this camera trust config */
  public CameraTrustConfig copy() {
    return new CameraTrustConfig(
        baseStdDevs.copy(),
        latencyThreshold,
        latencyMultiplier,
        fieldXYMargin,
        fieldZMargin,
        noisyDistance,
        distanceMultiplier,
        distanceMax,
        ambiguityThreshold,
        ambiguityMultiplier,
        ambiguityShifter,
        targetDivisor,
        differenceThreshold,
        differenceMultiplier);
  }
}
