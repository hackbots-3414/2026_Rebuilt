package frc.robot.subsystems.ledSubsystem.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.ledSubsystem.LedIO;
import frc.robot.subsystems.ledSubsystem.LedState;
import frc.robot.superstructure.StateManager;

public class InRange implements LedState{
     public boolean check(StateManager manager){
        return false;
    }

    public void apply(LedIO io){
        io.finalizeAnimation(new RGBWColor(Color.kBlue), LedIO.ANIMATION_TYPE.STROBE);
    }
    // Have to implement check
}
