package frc.robot.subsystems.led;

import java.util.List;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.led.ledStates.*;
import frc.robot.superstructure.StateManager;

public class Led extends SubsystemBase  {
    private LedState appliedState;
    private LedIO io;

    public Led(LedIO io) {
        super();
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
    
    public void update(StateManager stateManager) {
        for (LedState state : hierarchy) {
            if (!state.check(stateManager)) {
                continue;
            }
            if (appliedState == state) {
                break;
            }
            io.applyAnimation(state.apply(io));
            appliedState = state;
            break;
        }
    }
}
