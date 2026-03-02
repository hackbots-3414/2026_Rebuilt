package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class Climbed implements LedState {

    public boolean check(StateManager manager) {
        return manager.climbed().getAsBoolean();
    }

    public ControlRequest apply(LedIO io) {
        return io.createAnimation(new RGBWColor(), LedIO.ANIMATION_TYPE.RAINBOW);
    }
}
