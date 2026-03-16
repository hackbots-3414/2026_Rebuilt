package frc.robot.subsystems.led;

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

public class LedIO extends SubsystemBase {

  public static enum AnimationType {
    Twinkle, Strobe, Larson, Flash, Solid, Clear, Rainbow, Fade, Flow;
  }

  CANdle ledController = new CANdle(LedConstants.kCANdleId);
  int slot = 0;

  public LedIO() {
    super();
    ledController.getConfigurator().apply(LedConstants.kLedConfig);
  }

  public void applyAnimation(ControlRequest animation) {
    ledController.setControl(animation);
  }

  public void clearAnimation() {
    ledController.setControl(new EmptyAnimation(slot));
  }

  public ControlRequest createAnimation(RGBWColor color, AnimationType type) {
    switch (type) {
      case Twinkle:
        return new TwinkleAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case Strobe:
        return new StrobeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case Fade:
        return new SingleFadeAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case Rainbow:
        return new RainbowAnimation(LedConstants.startIndex, LedConstants.endIndex).withSlot(slot);
      case Larson:
        return new LarsonAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case Flow:
        return new ColorFlowAnimation(LedConstants.startIndex, LedConstants.endIndex).withColor(color).withSlot(slot);
      case Solid:
        return new SolidColor(LedConstants.startIndex, LedConstants.endIndex).withColor(color);
      default:
        return new EmptyAnimation(slot);
    }

  }
}