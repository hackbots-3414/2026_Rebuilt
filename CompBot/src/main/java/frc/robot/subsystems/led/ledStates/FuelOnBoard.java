package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class FuelOnBoard implements LedState{
     public boolean check(StateManager manager){
        return false;
    }

    public void apply(LedIO io){
        io.createAnimation(new RGBWColor(Color.kYellow), LedIO.ANIMATION_TYPE.STROBE);
    }
    // Have to implement check
}
