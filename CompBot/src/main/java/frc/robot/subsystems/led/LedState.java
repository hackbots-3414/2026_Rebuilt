package frc.robot.subsystems.led;

import com.ctre.phoenix6.controls.ControlRequest;

import frc.robot.superstructure.StateManager;

public interface LedState {
    public ControlRequest apply(LedIO io);
    public boolean check(StateManager manager);
}
