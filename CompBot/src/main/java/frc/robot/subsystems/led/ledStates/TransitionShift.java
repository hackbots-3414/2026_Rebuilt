package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedIO.ANIMATION_TYPE;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;
import frc.robot.util.ActivityCalculator;
import frc.robot.util.ActivityCalculator.HubActivity;

public class TransitionShift implements LedState {

    public boolean check (StateManager state) {
        return ActivityCalculator.when(ActivityCalculator.us()).getAsBoolean();
    }

    public ControlRequest apply (LedIO io) {
        Color color = (ActivityCalculator.us() == HubActivity.Red) ? Color.kRed : Color.kBlue;
        return io.createAnimation(new RGBWColor(color) , ANIMATION_TYPE.FLASH);
    }
    
}
