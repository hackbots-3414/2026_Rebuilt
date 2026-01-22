package frc.robot.subsystems.Turret;

import static edu.wpi.first.units.Units.Radians;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class Turret extends SubsystemBase {    

    private final TurretIO m_io;

    public Turret() {
        super();
        if (Robot.isReal()) {
            m_io = new TurretIOHardware();
        } else {
            m_io = new TurretIOSim();
        }
    }

    private void setPosition(Angle position) {
        m_io.setPosition(position);
    }

    /**
     * Go to zero from turret's current location - Aligns to robot's 0
     */
    public Command home() {
        return Commands.sequence(
            runOnce(() -> m_io.setPosition(Radians.zero())),
            Commands.waitUntil(ready()));
    }

    //TODO: Make real ready
    private BooleanSupplier ready() {
        return () -> false;
    }

    /**
     * Keeps the turret pointed at the correct target
     * @param configuration Robot's pose relative to target 
     */
    public void track(Pose2d configuration) {
        //Get current locations of turret and robot
        //TODO: Create turretIOInputs and pass those in here instead of turretconstants gear 1/2 cancoder
        double turretAngleRadians = getTurretAngleRadians(TurretConstants.kGear1CANcoder.getAbsolutePosition().getValueAsDouble(), 
                                                        TurretConstants.kGear2CANcoder.getAbsolutePosition().getValueAsDouble());
        double robotAngleRadians = configuration.getRotation().getRadians();

        //Calculate the turret's goal position in radians
        double turretGoal = robotAngleRadians - turretAngleRadians;

        setPosition(Radians.of(turretGoal));

    }

    /** 
     * @param gear1position Position off of the 28 tooth gear as read by the CANcoder on that axle
     * @param gear2position Position off of the 26 tooth gear as read by the CANcoder on that axle
     * @return: Turret position in radians in relation to robot, assuming that the opposite from center of the turret's blind spot is 0
     * @throws IllegalArgumentException Thrown if the turret location is bigger than the turret size
     */
    private double getTurretAngleRadians(double gear1position, double gear2position) throws IllegalArgumentException {
        
        //Changing gear names to match video so we can follow the video
        int m1 = TurretConstants.kGear1Size;
        int m2 = TurretConstants.kGear2Size;

        //Turn CANcoder's 0 to 1 measurement into teeth.
        int a1 = (int) Math.round(gear1position * TurretConstants.kGear1Size);
        int a2 = (int) Math.round(gear2position * TurretConstants.kGear2Size);

        //Calculating M, M1, M2, and their inverses.
        int M = m1*m2;
        int M1 = M/m1;
        int M2 = M/m2;

        int inverseM1 = 0;
        int inverseM2 = 0;

        //Going to m1 because the lowest possible value of inverseM1 to get a remainder of 1 is going to be less than m1.
        for (int i = 0; i <= m1; i++) {
            if ((M1 *i) % m1 == 1) {
                inverseM1 = i;
                break;
            }
        }

        for (int i = 0; i <= m2; i++) {
            if ((M2 *i) % m2 == 1) {
                inverseM2 = i;
                break;
            }
        }
        double turretPositionInTeeth = (a1*M1*inverseM1 + a2*M2*inverseM2)%M;

        if (turretPositionInTeeth > TurretConstants.kTurretSize) {
            throw new IllegalArgumentException("Current turret encoders reflect value over possible turret positions.");
        }

        double turretPositionInRadians = (turretPositionInTeeth * 2 * Math.PI / TurretConstants.kTurretSize) - Math.PI;

        return turretPositionInRadians;
    }

}
