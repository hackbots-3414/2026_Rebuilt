package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedConstants;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class EndGameWarning implements LedState {

    public boolean check(StateManager manager) {
        return DriverStation.getMatchTime() <= LedConstants.endgameWarning;
    }

    public void apply(LedIO io) {
        io.createAnimation(new RGBWColor(Color.kRed), LedIO.ANIMATION_TYPE.FLASH);
    }

}
