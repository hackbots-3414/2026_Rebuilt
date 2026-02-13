package frc.robot.subsystems.ledSubsystem.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import frc.robot.subsystems.ledSubsystem.LedIO;
import frc.robot.subsystems.ledSubsystem.LedState;
import frc.robot.superstructure.StateManager;

public class Climbed implements LedState{

    public boolean check(StateManager manager){
        return false;
    }

    public void apply(LedIO io){
        io.finalizeAnimation(new RGBWColor(), LedIO.ANIMATION_TYPE.RAINBOW);
    }

    // Implement how to check for Climb
}
