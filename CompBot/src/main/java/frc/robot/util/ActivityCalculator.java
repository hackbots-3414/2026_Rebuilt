package frc.robot.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ActivityCalculator {

  // We have to set default values
  private static HubActivity winner = HubActivity.Blue;
  private static HubActivity loser = HubActivity.Red;

  private static boolean failed = true;

  private static HubActivity teamHub;

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
  }

  public static void initialize() {
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

  public static HubStatus update() {
    if (DriverStation.isAutonomous()) {
      return new HubStatus(HubActivity.Both, DriverStation.getMatchTime());
    }

    if (failed || !DriverStation.isTeleopEnabled()) {
      return new HubStatus(HubActivity.Both, Double.POSITIVE_INFINITY);
    }

    double matchTime = DriverStation.getMatchTime();

    boolean winnerActive = false;
    double timeRemaining;

    if (matchTime > 130) {
      // Transition shift
      return new HubStatus(HubActivity.Both, matchTime - 130.0);
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
      return new HubStatus(HubActivity.Both, matchTime);
    }

    HubActivity currentAlliance = (winnerActive) ? winner : loser;
    return new HubStatus(currentAlliance, timeRemaining);
  }

  public static boolean is(HubActivity active, double timeCutoff) {
    HubStatus status = update();
      return (status.active == active || status.active == HubActivity.Both)
          && status.timeRemaining <= timeCutoff;
  }

  public static boolean is(HubActivity active) {
    return is(active, Double.POSITIVE_INFINITY);
  }

  /**
   * Returns a trigger that is true when the given hub status is active AND the time remaining for
   * that status is less than the provided time remaining.
   * 
   * @param active The active hub
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
}
