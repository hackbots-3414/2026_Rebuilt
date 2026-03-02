package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.LedConstants;
import frc.robot.subsystems.led.LedIO;
import frc.robot.subsystems.led.LedState;
import frc.robot.superstructure.StateManager;


public class BadController implements LedState {

    public boolean check(StateManager manager) {
        return badController();
    }
    public ControlRequest apply(LedIO io) {
        return io.createAnimation(new RGBWColor(Color.kRed), LedIO.ANIMATION_TYPE.STROBE);
    }

    private boolean badController() {
    boolean driverConnected = DriverStation.isJoystickConnected(LedConstants.kDriverPort);// Have to be changed to
                                                                                          // Binding Constants
    boolean operatorConnected = DriverStation.isJoystickConnected(LedConstants.kOperatorPort); // Have to Be Changed to
                                                                                               // Binding Constants

    if (!driverConnected || !operatorConnected)
      return true;

    String driverName = DriverStation.getJoystickName(LedConstants.kDriverPort).toLowerCase(); // Have to Be Changed to
                                                                                               // Binding Constants
    String operatorName = DriverStation.getJoystickName(LedConstants.kOperatorPort).toLowerCase();// Have to Be Changed
                                                                                                  // to Binding
                                                                                                  // Constants

    boolean driverOk = driverName.contains(LedConstants.dragonReinsName);

    boolean operatorOk = operatorName.contains(LedConstants.ps5Name);

    return !(driverOk && operatorOk);
  }
}