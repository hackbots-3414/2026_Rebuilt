package frc.robot.subsystems.ledSubsystem.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.ledSubsystem.LedConstants;
import frc.robot.subsystems.ledSubsystem.LedIO;
import frc.robot.subsystems.ledSubsystem.LedState;
import frc.robot.superstructure.StateManager;

public class EndGameWarning implements LedState{

     public boolean check(StateManager manager){
        return manager.getMatchTime() <= LedConstants.endgameWarning;
    }

    public void apply(LedIO io){
        io.finalizeAnimation(new RGBWColor(Color.kRed), LedIO.ANIMATION_TYPE.FLASH);
    }
    
}
