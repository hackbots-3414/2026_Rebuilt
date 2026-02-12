package frc.robot.subsystems.climber;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.CommandBasedTest;
import frc.robot.subsystems.climber.ClimberIO.ClimberIOInputs;

public class ClimberTest extends CommandBasedTest {
    
    @Test
    public void climbTest() {
        ClimberIO mockClimbIO = mock(ClimberIO.class);
        doAnswer(invocation -> {
        ClimberIOInputs inputs = invocation.getArgument(0, ClimberIOInputs.class);
        inputs.velocity = ClimberConstants.kVelocity;
        inputs.position = ClimberConstants.ClimberPositions.NotClimbed.position;

        // It's a void method, so we're supposed to return null.
        return null;
        }).when(mockClimbIO).updateInputs(Mockito.any(ClimberIOInputs.class));

    Climber climber = new Climber(mockClimbIO);
    CommandScheduler.getInstance().run();
    // Ensure we update the inputs every periodic run
    verify(mockClimbIO).updateInputs(Mockito.any(ClimberIOInputs.class));

    CommandScheduler.getInstance().schedule(climber.climb(ClimberConstants.ClimberPositions.Level1));
    CommandScheduler.getInstance().run();

    //Can't verify set voltage because DC Motor Sim just teleports (DCMotorSim has no option to set position control)
    verify(mockClimbIO).setPosition(ClimberConstants.ClimberPositions.Level1.position);

    CommandScheduler.getInstance().schedule(climber.climb(ClimberConstants.ClimberPositions.NotClimbed));
    CommandScheduler.getInstance().run();

    verify(mockClimbIO).setPosition(ClimberConstants.ClimberPositions.NotClimbed.position);

    }

}