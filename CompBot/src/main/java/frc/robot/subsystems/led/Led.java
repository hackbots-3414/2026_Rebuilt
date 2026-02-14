package frc.robot.subsystems.led;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.led.ledStates.*;
import frc.robot.superstructure.StateManager;

public class Led extends SubsystemBase  {
    private LedState appliedState;
    private StateManager manager;
    private LedIO io;
    public Led(StateManager manager, LedIO io) {
        this.manager = manager;
        this.io = io;
    }
    //todo assign priority values to each ledstate (ORDERED)
    private List<LedState> hierarchy = List.of(
        new BadController(),
        new Climbed(),
        new EndGameWarning(),
        new EndGameAlert(),
        new FuelOnBoard(),
        new ShootReady(),
        new Default()
    );
    
    @Override
    public void periodic() {
        for (LedState state : hierarchy) {
            if (!state.check(manager)) {
                continue;
            }
            if (appliedState == state) {
                break;
            }
            state.apply(io);
            appliedState = state;
            break;
        }
    }
}
