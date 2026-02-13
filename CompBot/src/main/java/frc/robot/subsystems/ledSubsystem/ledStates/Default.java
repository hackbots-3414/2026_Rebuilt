package frc.robot.subsystems.ledSubsystem.ledStates;

import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.ledSubsystem.LedIO;
import frc.robot.subsystems.ledSubsystem.LedState;
import frc.robot.superstructure.StateManager;

public class Default implements LedState{
    public boolean check(StateManager manager){
        return !(DriverStation.isTeleopEnabled() || DriverStation.isAutonomousEnabled());
    }
    public void apply(LedIO io){
        io.finalizeAnimation(new RGBWColor(Color.kPurple), LedIO.ANIMATION_TYPE.LARSON);
    }


}
