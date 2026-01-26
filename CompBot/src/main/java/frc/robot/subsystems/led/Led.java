
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.subsystems.led;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.hardware.DeviceIdentifier;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.configs.CANdleConfigurator;
import com.ctre.phoenix6.configs.LEDConfigs;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Led extends SubsystemBase {
  @SuppressWarnings("unused")
  private final Logger m_logger = LoggerFactory.getLogger(Led.class);
  private static double matchTime = 0;

  private boolean fuelOnBoard = false;
  private boolean climbed = false;
  private boolean inAuton = false;
  private boolean inTeleop = false;
  private boolean isInRange = false;

  private int selectedSlot = 0;

  private static enum LED_MODE {
    END_GAME_ALERT, END_GAME_WARNING, FUEL_ON_BOARD, DEFAULT, CLIMBED, IN_RANGE, BADCONTROLLER,
  };

  private static enum ANIMATION_TYPE {
    TWINKLE, STROBE, LARSON, FLASH, SOLID, CLEAR, RAINBOW, FADE, FLOW;
  };

  private LED_MODE mode = null;

  private CANdle ledcontroller = new CANdle(LedConstants.candle1);

  public Led() {
    super();
    LEDConfigs config = new LEDConfigs();
    CANdleConfigurator configurator = new CANdleConfigurator(new DeviceIdentifier());
    config.BrightnessScalar = 0.7; // dim the LEDs to 70% brightness
    configurator.apply(config, 20);
    StrobeAnimation strobe = new StrobeAnimation(0, 30);
    strobe.Slot = 1;
    strobe.Color = new RGBWColor();
    ledcontroller.setControl(strobe);

  }

  @Override
  public void periodic() {
    matchTime = (DriverStation.getMatchType() == MatchType.None) ? Double.POSITIVE_INFINITY
        : DriverStation.getMatchTime();
    inAuton = DriverStation.isAutonomousEnabled();
    inTeleop = DriverStation.isTeleopEnabled();

    if (badController()) {
      if (modeIsSet(LED_MODE.BADCONTROLLER)) {
        return;
      }
      setMode(LED_MODE.BADCONTROLLER);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kRed), selectedSlot, ANIMATION_TYPE.STROBE));
      return;
    }
    if (climbed) {
      if (modeIsSet(LED_MODE.CLIMBED)) {
        return;
      }
      setMode(LED_MODE.CLIMBED);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          null, selectedSlot, ANIMATION_TYPE.RAINBOW));
      return;
    }
    if (!(inTeleop || inAuton)) {
      if (modeIsSet(LED_MODE.DEFAULT)) {
        return;
      }
      setMode(LED_MODE.DEFAULT);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kPurple), selectedSlot,
          ANIMATION_TYPE.FADE));
      return;
    }
    if (inTeleop && inEndgame()) {
      if (modeIsSet(LED_MODE.END_GAME_ALERT)) {
        return;
      }
      setEndgame();
      return;
    }
    if (fuelOnBoard && isInRange) {
      if (modeIsSet(LED_MODE.IN_RANGE)) {
        return;
      }
      setMode(LED_MODE.IN_RANGE);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kBlue), selectedSlot,
          ANIMATION_TYPE.SOLID));
      return;
    }
    if (fuelOnBoard) {
      if (modeIsSet(LED_MODE.FUEL_ON_BOARD)) {
        return;
      }
      setMode(LED_MODE.FUEL_ON_BOARD);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kYellow), selectedSlot,
          ANIMATION_TYPE.SOLID));
      return;
    }
    if (modeIsSet(LED_MODE.DEFAULT)) {
      return;
    }
    ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
        new RGBWColor(Color.kPurple), selectedSlot, ANIMATION_TYPE.FLASH));
  }

  private ControlRequest createAnimation(int startIndex, int endIndex, RGBWColor color, int slot, ANIMATION_TYPE type) {
    switch (type) {
      case TWINKLE:
        return new TwinkleAnimation(startIndex, endIndex).withColor(color).withSlot(slot);
      case STROBE:
        return new StrobeAnimation(startIndex, endIndex).withColor(color).withSlot(slot);
      case FADE:
        return new SingleFadeAnimation(startIndex, endIndex).withColor(color).withSlot(slot);
      case RAINBOW:
        return new RainbowAnimation(startIndex, endIndex).withSlot(slot);
      case LARSON:
        return new LarsonAnimation(startIndex, endIndex).withColor(color).withSlot(slot);
      case CLEAR:
        return new EmptyAnimation(slot);
      case FLOW:
        return new ColorFlowAnimation(startIndex, endIndex).withColor(color).withSlot(slot);
      case SOLID:
        return new SolidColor(startIndex, endIndex).withColor(color);
      default:
        return new EmptyAnimation(slot);
    }

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

  public boolean inEndgame() {
    return matchTime <= LedConstants.endgameWarning;
  }

  public boolean inEndgameAlert() {
    return matchTime <= LedConstants.endgameAlert;
  }

  public void setEndgame() {
    if (inEndgameAlert()) {
      setMode(LED_MODE.END_GAME_ALERT);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kRed), 0, ANIMATION_TYPE.STROBE));
    }
    if (inEndgame()) {
      setMode(LED_MODE.END_GAME_WARNING);
      ledcontroller.setControl(createAnimation(LedConstants.startIndex, LedConstants.endIndex,
          new RGBWColor(Color.kRed), 0, ANIMATION_TYPE.SOLID));

    }
  }

  public boolean modeIsSet(LED_MODE mode) { // checks if mode is already set
    return (this.mode != mode);
  }

  public void setMode(LED_MODE mode) { // sets mode if mode is not already set
    this.mode = mode;
  }
}