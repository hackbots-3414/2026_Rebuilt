package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;
import frc.robot.util.ActivityCalculator;
import frc.robot.util.ActivityCalculator.HubActivity;

public class HubActive implements LedState {

    public boolean check(StateManager manager) {
        return ActivityCalculator.is(ActivityCalculator.us());
    }

    public ControlRequest apply(LedIO io) {
        
        if (ActivityCalculator.us().equals(HubActivity.Red)) {
            return io.createAnimation(new RGBWColor(Color.kRed), LedIO.ANIMATION_TYPE.SOLID);
        }
        return io.createAnimation(new RGBWColor(Color.kBlue), LedIO.ANIMATION_TYPE.SOLID);
    }

}
