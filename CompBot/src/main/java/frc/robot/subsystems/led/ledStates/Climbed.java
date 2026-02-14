package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class Climbed implements LedState {

    public boolean check(StateManager manager) {
        return manager.climbed().getAsBoolean();
    }

    public void apply(LedIO io) {
        io.createAnimation(new RGBWColor(), LedIO.ANIMATION_TYPE.RAINBOW);
    }

    // Implement how to check for Climb
}
