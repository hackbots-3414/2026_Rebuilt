package frc.robot.subsystems.led;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.configs.LEDConfigs;
import com.ctre.phoenix6.signals.StripTypeValue;

import frc.robot.util.RobotIdentifier;
import frc.robot.util.StatusSignalUtil;
import frc.robot.util.RobotIdentifier.RobotId;

public class LedConstants {
    // Offsets & numLEds & IDS

    public static final int kCANdleId = 5;
    public static final CANBus kCanbus = RobotIdentifier.id() == RobotId.TestBot ? StatusSignalUtil.canivore : StatusSignalUtil.rio;
    public static final CANdleConfiguration kLedConfig = new CANdleConfiguration()
        .withLED(new LEDConfigs()
            .withStripType(RobotIdentifier.id() == RobotId.TestBot ? StripTypeValue.GRB : StripTypeValue.RGB));
    
    public static final int startIndex = 0;
    public static final int endIndex = 53;

    public static final int endgameWarning = 30;
    public static final int endgameAlert = 15;


    public static final String driverName = "controller";
    public static final String operatorName= "dual";

    // Animation Attributes

    public static final double kStrobeRate = 2;
    public static final double kFlashRate = 1.0;
}
