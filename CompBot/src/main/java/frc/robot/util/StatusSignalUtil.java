package frc.robot.util;

import java.util.ArrayList;
import java.util.List;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;

/**
 * A class with helper static methods to register status signals. This class lets all status signals
 * update at once, instead of each and every time they're read individually, bettering performance.
 */
@SuppressWarnings("rawtypes")
public class StatusSignalUtil {
  private static List<BaseStatusSignal> rioSignals = new ArrayList<>();
  private static List<BaseStatusSignal> canivoreSignals = new ArrayList<>();

  public static void registerRioSignals(StatusSignal... signals) {
    for (StatusSignal signal : signals) {
      rioSignals.add(signal);
    }
  }

  public static void registerCANivoreSignals(StatusSignal... signals) {
    for (StatusSignal signal : signals) {
      canivoreSignals.add(signal);
    }
  }

  public static void refreshAll() {
    BaseStatusSignal.refreshAll(rioSignals);
    BaseStatusSignal.refreshAll(canivoreSignals);
  }
}

