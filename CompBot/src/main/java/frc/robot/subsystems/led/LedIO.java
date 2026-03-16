package frc.robot.subsystems.led;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.StatusSignalUtil;

public class LedIO extends SubsystemBase {

  public static enum AnimationType {
    Twinkle, Strobe, Larson, Flash, Solid, Clear, Rainbow, Fade, Flow;
  }

  CANdle ledController = new CANdle(LedConstants.kCANdleId, LedConstants.kCanbus);
  int slot = 0;

  public LedIO() {
    super();
    ledController.getConfigurator().apply(LedConstants.kLedConfig);
  }

  public void applyAnimation(ControlRequest animation) {
    clearAnimation();
    ledController.setControl(animation);
  }

  public void clearAnimation() {
    ledController.setControl(new EmptyAnimation(slot));
  }

  public ControlRequest createAnimation(RGBWColor color, AnimationType type) {
    return switch (type) {
          case Twinkle -> new TwinkleAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
          case Strobe -> new StrobeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot).withFrameRate(LedConstants.kStrobeRate);
          case Fade -> new SingleFadeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
          case Rainbow -> new RainbowAnimation(LedConstants.startIndex, LedConstants.endIndex).withSlot(slot);
          case Larson -> new LarsonAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
          case Flow -> new ColorFlowAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
          case Solid -> new SolidColor(LedConstants.startIndex, LedConstants.endIndex).withColor(color);
          case Flash -> new StrobeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot).withFrameRate(LedConstants.kFlashRate);
          case Clear -> new EmptyAnimation(slot);
    };
  }
}