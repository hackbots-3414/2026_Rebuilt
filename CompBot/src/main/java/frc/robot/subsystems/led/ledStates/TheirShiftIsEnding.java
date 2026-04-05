package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedIO.AnimationType;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;
import frc.robot.util.ActivityCalculator;

public class TheirShiftIsEnding implements LedState {
    public boolean check(StateManager state) {
        return ActivityCalculator.is(ActivityCalculator.other(), 3) && DriverStation.isEnabled();
    }

    public ControlRequest apply(LedIO io) {
        return io.createAnimation(new RGBWColor(Color.kWhite), AnimationType.Solid);
    }
}
