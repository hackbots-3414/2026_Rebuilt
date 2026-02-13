package frc.robot.subsystems.ledSubsystem;

import frc.robot.superstructure.StateManager;

public interface LedState {

    public void apply(LedIO io);
    
    public boolean check(StateManager manager);
}
