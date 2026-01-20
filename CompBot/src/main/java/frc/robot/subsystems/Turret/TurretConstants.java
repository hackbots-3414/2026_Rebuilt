package frc.robot.subsystems.Turret;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

public class TurretConstants {

    protected static final TalonFX turretMotor = new TalonFX(11);

    //Find these actual values
    protected static final CANcoder gear1CANcoder = new CANcoder(0);
    protected static final CANcoder gear2CANcoder = new CANcoder(1);

    protected static final int gear1Size = 28;
    protected static final int gear2Size = 26;
    protected static final int turretSize = 100;

    
}
