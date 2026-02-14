package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class Default implements LedState{
    public boolean check(StateManager manager){
        return !(DriverStation.isTeleopEnabled() || DriverStation.isAutonomousEnabled());
    }
    public void apply(LedIO io){
        io.createAnimation(new RGBWColor(Color.kPurple), LedIO.ANIMATION_TYPE.LARSON);
    }


}
