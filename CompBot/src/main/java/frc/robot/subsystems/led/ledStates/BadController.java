package frc.robot.subsystems.led.ledStates;

import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.binding.BindingConstants;
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
    boolean driverConnected = DriverStation.isJoystickConnected(BindingConstants.Driver.kDriveControllerPort);
    boolean operatorConnected = DriverStation.isJoystickConnected(BindingConstants.Operator.kOperatorControllerPort);

    if (!driverConnected || !operatorConnected) {
      return true;
    }

    String driverName = DriverStation.getJoystickName(BindingConstants.Driver.kDriveControllerPort).toLowerCase();
    String operatorName = DriverStation.getJoystickName(BindingConstants.Operator.kOperatorControllerPort)
        .toLowerCase();

    boolean driverOk = driverName.contains(LedConstants.driverName);

    boolean operatorOk = operatorName.contains(LedConstants.operatorName);

    return !(driverOk && operatorOk);
  }
}