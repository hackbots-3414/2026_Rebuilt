package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class ShootReady implements LedState{
    
    public boolean check(StateManager manager) {
        return manager.shootReady().getAsBoolean();
    }

    public void apply(LedIO io) {
        io.createAnimation(new RGBWColor(Color.kPurple), LedIO.ANIMATION_TYPE.LARSON);
    }

   
    
}
