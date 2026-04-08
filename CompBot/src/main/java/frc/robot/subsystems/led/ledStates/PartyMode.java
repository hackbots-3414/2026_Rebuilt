package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.subsystems.led.LedIO.AnimationType;
import frc.robot.superstructure.StateManager;

public class PartyMode implements LedState {
  public boolean check(StateManager state) {
    return state.partying.getAsBoolean();
  }

  public ControlRequest apply(LedIO io) {
    return io.createAnimation(new RGBWColor(Color.kWhite), AnimationType.Rainbow);
  }
}
