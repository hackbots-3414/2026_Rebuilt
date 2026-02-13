package frc.robot.subsystems.ledSubsystem;

import com.ctre.phoenix6.configs.CANdleConfigurator;
import com.ctre.phoenix6.configs.LEDConfigs;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.hardware.DeviceIdentifier;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LedIO extends SubsystemBase {
      
  public static enum ANIMATION_TYPE {
    TWINKLE, STROBE, LARSON, FLASH, SOLID, CLEAR, RAINBOW, FADE, FLOW;
  }
  
    CANdle ledcontroller = new CANdle(LedConstants.candle1);
    int slot = 2;
    public LedIO() {
        super();
        LEDConfigs config = new LEDConfigs();
        CANdleConfigurator configurator = new CANdleConfigurator(new DeviceIdentifier());
        config.BrightnessScalar = 0.7; // dim the LEDs to 70% brightness
        configurator.apply(config, 20);
    }
    public void applyAnimation(ControlRequest animation) {
        ledcontroller.setControl(animation);
    }
    public void clearAnimation(){
        ledcontroller.setControl(new EmptyAnimation(slot));
    }
    public ControlRequest finalizeAnimation(RGBWColor color, ANIMATION_TYPE type) {
    switch (type) {
      case TWINKLE:
        return new TwinkleAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case STROBE:
        return new StrobeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case FADE:
        return new SingleFadeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case RAINBOW:
        return new RainbowAnimation(LedConstants.startIndex, LedConstants.endIndex).withSlot(slot);
      case LARSON:
        return new LarsonAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case FLOW:
        return new ColorFlowAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case SOLID:
        return new SolidColor(LedConstants.startIndex, LedConstants.endIndex).withColor(color);
      default:
        return new EmptyAnimation(slot);
    }

  }
}