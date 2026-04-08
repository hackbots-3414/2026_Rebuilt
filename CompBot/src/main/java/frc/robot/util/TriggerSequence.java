package frc.robot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * A helper class that is used to create a trigger corresponding to the press of multiple triggers.
 */
public class TriggerSequence {
  private int count;
  private final int total;

  private TriggerSequence(Trigger[] triggers, int[] sequence) {
    total = sequence.length;

    count = 0;
    for (int i = 0; i < triggers.length; i++) {
      int j = i;
      triggers[j].onTrue(Commands.runOnce(() -> {
        if (count == sequence.length) {
          count = 0;
        }
        if (j == sequence[count]) {
          count++;
        } else {
          count = 0;
        }
      }));
    }
  }

  /**
   * Creates a trigger that is true once the given triggers are pressed in the correct order.
   *
   * @param triggers the triggers that should be considered for the sequence.
   * @param sequence in order, the indexes of the triggers in the provided list that need to be
   *        pressed.
   */
  public static Trigger create(Trigger[] triggers, int[] sequence) {
    TriggerSequence password = new TriggerSequence(triggers, sequence);
    return new Trigger(() -> password.count == password.total);
  }

  /**
   * Creates a trigger sequence from a controller
   *
   * @param controller A controller object that supplies triggers
   * @param sequence The button IDs, in the order they should be pressed.
   */
  public static Trigger fromController(CommandGenericHID controller, int... sequence) {
    int[] realSequence = new int[sequence.length];
    List<Trigger> triggers = new ArrayList<>();
    // seenTriggers maps controller button IDs from "sequence" to the index of the trigger in
    // "triggers". This lets us take a controller ID that we've already seen and turn it into the
    // index of a trigger in "triggers".
    Map<Integer, Integer> seenTriggers = new HashMap<>();
    for (int i = 0; i < sequence.length; i++) {
      int triggerId = sequence[i];
      if (seenTriggers.containsKey(triggerId)) {
        // We already have an ID for this one.
        realSequence[i] = seenTriggers.get(triggerId);
      } else {
        realSequence[i] = seenTriggers.size();
        seenTriggers.put(sequence[i], realSequence[i]);
      }
    }
    return create(triggers.toArray(new Trigger[] {}), realSequence);
  }
}
