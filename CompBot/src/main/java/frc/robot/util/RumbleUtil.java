package frc.robot.util;

import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;

public class RumbleUtil {

  private static final RumbleType BOTH = RumbleType.kBothRumble;

  /**
   * Constant, labelled strengths for the controller. This is better than passing
   * around raw doubles everywhere because this ensure the data makes sense and
   * reads declaratively.
   */
  public enum RumbleStrength {
    Low(0.2),
    Medium(0.6),
    High(1.0);

    protected final double output;

    private RumbleStrength(double output) {
      this.output = output;
    }
  }

  public static Command rumble(CommandGenericHID controller, RumbleStrength strength) {
    return Commands.sequence(
        Commands.runOnce(() -> controller.setRumble(BOTH, strength.output)),
        Commands.idle())

        .finallyDo(() -> controller.setRumble(BOTH, 0))
        .ignoringDisable(true);
  }

  public static Command alert(CommandGenericHID controller, RumbleStrength strength, double alertTime, double inactiveTime) {
    return Commands.repeatingSequence(
      rumble(controller, strength).withTimeout(alertTime),
      Commands.waitSeconds(inactiveTime)
    )
        .ignoringDisable(true);
  }
}
