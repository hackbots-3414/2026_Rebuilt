package frc.robot.aiming;

import frc.robot.superstructure.StateManager;

/**
 * An interface representing a method of aim-calculation; i.e. calculating what shot parameters are
 * necessary for a given robot configuration
 */
public abstract class AimStrategy {
  public AimParams params = new AimParams();

  /**
   * Updates the AimParams with the new calculated shot based on the robot's state
   *
   * @param state The {@link frc.robot.superstructure.Superstructure}'s state.
   */
  public abstract AimParams update(StateManager state);
}
