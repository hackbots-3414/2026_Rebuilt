package frc.robot.subsystems.led;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.configs.LEDConfigs;
import com.ctre.phoenix6.signals.StripTypeValue;

public class LedConstants {
    // Offsets & numLEds & IDS

    public static final int kCANdleId = 5;
    public static final CANdleConfiguration kLedConfig = new CANdleConfiguration()
        .withLED(new LEDConfigs()
            .withStripType(StripTypeValue.RGB));
    
    public static final int startIndex = 0;
    public static final int endIndex = 53;

    public static final int endgameWarning = 30;
    public static final int endgameAlert = 15;


    public static final String dragonReinsName = "spark";
    public static final String ps5Name= "Dual";

    // Animation Attributes

    public static final double flashSpeed = 0.75;
    public static final double strobeSpeed = 0.1;



    // Binding Constants for Now. This have to be moved to actual Constants
    public static final int kDriverPort = 0;
    public static final int kOperatorPort = 1;
}
