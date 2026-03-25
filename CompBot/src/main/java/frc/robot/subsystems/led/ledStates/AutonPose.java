package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;

public class AutonPose implements LedState {

    public boolean check(StateManager manager) {
        return manager.inAutonStartPose.getAsBoolean() && DriverStation.isDisabled() && DriverStation.isAutonomous();
    }

    public ControlRequest apply(LedIO io) {
        return io.createAnimation(new RGBWColor(Color.kMaroon), LedIO.AnimationType.Epilepsy);
    }
}
