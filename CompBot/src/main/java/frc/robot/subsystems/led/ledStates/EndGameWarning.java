package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedConstants;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class EndGameWarning implements LedState {

    public boolean check(StateManager manager) {
        return DriverStation.getMatchTime() < LedConstants.endgameWarning && DriverStation.isTeleopEnabled() && DriverStation.getMatchType() != MatchType.None;
    }

    public ControlRequest apply(LedIO io) {
       return io.createAnimation(new RGBWColor(Color.kOrange), LedIO.AnimationType.Strobe);
    }

}
