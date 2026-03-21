package frc.robot.util;

import java.text.DecimalFormat;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ActivityCalculator {

  private static final DecimalFormat df = new DecimalFormat("#.00");

  // We have to set default values
  private static HubActivity winner = HubActivity.Blue;
  private static HubActivity loser = HubActivity.Red;

  private static boolean failed = true;

  private static HubActivity teamHub;

  private static double end = Double.POSITIVE_INFINITY;

  private static final Alert noData = new Alert("FMS has not communicated the winner of auto", AlertType.kWarning);

  public enum HubActivity {
    Red, Blue, Both;
  }

  public record HubStatus(HubActivity active, double timeRemaining) {

    /** Returns a color-coded version of the current alliance hub */
    public String color(HubActivity only) {
      if (only != active() && active() != HubActivity.Both) {
        return "#322E47";
      }
      String base = switch (active()) {
        case Both -> "#73D966";
        case Blue -> "#5672C7";
        case Red -> "#D6365E";
      };
      String low = switch (active) {
        case Both -> "#86A689";
        case Blue -> "#787E94";
        case Red -> "#823434";
      };
      return timeRemaining() <= 5 ? low : base;
    }

    public String timeText() {
      return df.format(timeRemaining());
    }
  }

  private static HubStatus status = new HubStatus(HubActivity.Both, Double.POSITIVE_INFINITY);

  public static void readGameData() {
    if (DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red) {
      teamHub = HubActivity.Red;
    } else {
      teamHub = HubActivity.Blue;
    }

    failed = false;

    String gameData = DriverStation.getGameSpecificMessage();
    if (gameData.length() == 0) {
      DriverStation.reportError("No game data received", false);
      // We have to give these variables values so that we don't get null pointer
      // exception later.
      winner = HubActivity.Blue;
      loser = HubActivity.Red;
      failed = true;
      return;
    }

    switch (gameData.toLowerCase().charAt(0)) {
      case 'b':
        winner = HubActivity.Blue;
        loser = HubActivity.Red;
        return;
      case 'r':
        winner = HubActivity.Red;
        loser = HubActivity.Blue;
        return;
      default:
        winner = HubActivity.Red;
        loser = HubActivity.Blue;
        failed = true;
        DriverStation.reportError("Invalid data from the Driver Station: '" + gameData + "'",
            false);
    }
  }

  public static boolean ok() {
    return !failed;
  }

  public static void startTimer() {
    end = Timer.getTimestamp() + 140.0; // 140s in teleop
  }

  public static void update() {
    if (DriverStation.isAutonomous()) {
      setStatus(HubActivity.Both, DriverStation.getMatchTime());
      return;
    }

    noData.set(failed);

    if (failed || !DriverStation.isTeleopEnabled()) {
      setStatus(HubActivity.Both, Double.POSITIVE_INFINITY);
      return;
    }

    // double matchTime = end - Timer.getTimestamp();
    double matchTime = DriverStation.getMatchTime();

    boolean winnerActive = false;
    double timeRemaining;

    if (matchTime > 130) {
      // Transition shift
      setStatus(HubActivity.Both, matchTime - 130.0);
      return;
    } else if (matchTime > 105) {
      // First alliance shift
      winnerActive = false;
      timeRemaining = matchTime - 105;
    } else if (matchTime > 80) {
      // Second alliance shift
      winnerActive = true;
      timeRemaining = matchTime - 80;
    } else if (matchTime > 55) {
      // Third alliance shift
      winnerActive = false;
      timeRemaining = matchTime - 55;
    } else if (matchTime > 30) {
      // Fourth alliance shift
      winnerActive = true;
      timeRemaining = matchTime - 30;
    } else {
      // Endgame, match time IS time remaining.
      setStatus(HubActivity.Both, matchTime);
      return;
    }

    HubActivity currentAlliance = (winnerActive) ? winner : loser;
    setStatus(currentAlliance, timeRemaining);
  }

  private static void setStatus(HubActivity active, double timeRemaining) {
    status = new HubStatus(active, timeRemaining);
  }

  public static boolean is(HubActivity active, double timeCutoff) {
    return (status.active == active || status.active == HubActivity.Both || active == HubActivity.Both)
        && status.timeRemaining <= timeCutoff;
  }

  public static boolean is(HubActivity active) {
    return is(active, Double.POSITIVE_INFINITY);
  }

  /**
   * Returns a trigger that is true when the given hub status is active AND the
   * time remaining for
   * that status is less than the provided time remaining.
   * 
   * @param active     The active hub
   * @param timeCutoff The time until the status changes
   */
  public static Trigger when(HubActivity active, double timeCutoff) {
    return new Trigger(() -> is(active, timeCutoff));
  }

  public static Trigger when(HubActivity active) {
    return when(active, Double.POSITIVE_INFINITY);
  }

  public static HubActivity us() {
    return teamHub;
  }

  public static HubActivity other() {
    return teamHub == HubActivity.Blue ? HubActivity.Red : HubActivity.Blue;
  }

  /** Returns the last cached hub status. Only calls to {@code update()} will actually change this value. */
  public static HubStatus status() {
    return status;
  }

  public static void startLogging() {
    OnboardLogger logger = new OnboardLogger("Hub");
    logger.registerString("Status", () -> status.active().toString());
    logger.registerBoolean("Enabled", () -> is(us()));
    logger.registerDouble("Time Remaining", () -> status.timeRemaining());
    logger.registerBoolean("Valid DS Data", () -> !failed);
  }
}
