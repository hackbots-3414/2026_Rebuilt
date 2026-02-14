package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedConstants;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class EndGameAlert implements LedState {
    public boolean check(StateManager manager){
        return DriverStation.getMatchTime() < LedConstants.endgameAlert;
    }

    public ControlRequest apply(LedIO io){
       return  io.createAnimation(new RGBWColor(Color.kRed), LedIO.ANIMATION_TYPE.STROBE);
    }

}
