// package frc.robot.subsystems.led;


// import static edu.wpi.first.units.Units.Degrees;
// import static edu.wpi.first.units.Units.MetersPerSecond;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.doAnswer;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import com.ctre.phoenix6.controls.ControlRequest;
// import com.ctre.phoenix6.hardware.CANdle;

// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.wpilibj2.command.CommandScheduler;
// import edu.wpi.first.wpilibj2.command.button.Trigger;
// import frc.robot.CommandBasedTest;
// import frc.robot.aiming.AimParams;
// import frc.robot.subsystems.led.ledStates.BadController;
// import frc.robot.superstructure.StateManager;


// public class LedTest  extends CommandBasedTest{
//     @Test
//     public void ledTest() {
//      LedIO mockLedIO = mock(LedIO.class);
//      StateManager mockStateManager = mock(StateManager.class);


//      Led led = new Led(mockStateManager,mockLedIO);
//      CommandScheduler.getInstance().run();
    
//      verify(mockLedIO.ledcontroller).setControl(Mockito.any(ControlRequest.class));

     
//     }



























//         // LedIO mockLedIO =  new LedIO();
//         // mockLedIO.ledcontroller = mock(CANdle.class);
//         // StateManager mockStateManager = mock(StateManager.class);
//         // Led mockLed = new Led(mockStateManager, mockLedIO);
//         // mockLed.appliedState = new BadController();


//         // doAnswer(invocation -> {
//         //     System.out.print(mockLed.appliedState);
//         //     return null;
//         // }).when(mockLedIO.ledcontroller).setControl(Mockito.any(ControlRequest.class));

//         // CommandScheduler.getInstance().run();

//         // BadController mockbadcontroller = mock(BadController.class);



//     }

