package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class ShootReady implements LedState{
    
    public boolean check(StateManager manager) {
        return SmartDashboard.getBoolean("/LedState ShootReady", false);
        // return manager.shootReady().getAsBoolean();
    }

    public ControlRequest apply(LedIO io) {
        return io.createAnimation(new RGBWColor(Color.kBlue), LedIO.ANIMATION_TYPE.FLOW);

    }

   
    
}
