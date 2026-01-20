package frc.robot.subsystems.Turret;

import edu.wpi.first.math.geometry.Pose2d;

public class Turret {    

    private void setPosition(double position) {
            
    }

    private void setMotor(double speed) {
        TurretConstants.turretMotor.set(speed);
    }

    /**
     * Go to zero from turret's current location - Aligns to robot's 0
     */
    public void home() {
            setPosition(0);
    }

    /**
     * Keeps the turret pointed at the correct target
     * @param configuration Robot's pose relative to target 
     */
    public void track(Pose2d configuration) {
        //Get current locations of turret and robot
        double turretAngleRadians = getTurretAngleRadians(TurretConstants.gear1CANcoder.getAbsolutePosition().getValueAsDouble(), 
                                                        TurretConstants.gear2CANcoder.getAbsolutePosition().getValueAsDouble());
        double robotAngleRadians = configuration.getRotation().getRadians();

        //Calculate the turret's goal position in radians
        double turretGoal = robotAngleRadians - turretAngleRadians;

        setPosition(turretGoal);

    }

    /**
     * Calibrates the turret by resetting both encoders to 0.
     */
    public void calibrate() {
        TurretConstants.gear1CANcoder.setPosition(0);
        TurretConstants.gear2CANcoder.setPosition(0);
    }

    /** 
     * @param gear1position Position off of the 28 tooth gear as read by the CANcoder on that axle
     * @param gear2position Position off of the 26 tooth gear as read by the CANcoder on that axle
     * @return: Turret position in radians in relation to robot, assuming that the opposite from center of the turret's blind spot is 0
     * @throws IllegalArgumentException Thrown if the turret location is bigger than the turret size
     */
    private double getTurretAngleRadians(double gear1position, double gear2position) throws IllegalArgumentException {
        
        //Changing gear names to match video so we can follow the video
        int m1 = TurretConstants.gear1Size;
        int m2 = TurretConstants.gear2Size;

        //Turn CANcoder's 0 to 1 measurement into teeth.
        int a1 = (int) Math.round(gear1position * TurretConstants.gear1Size);
        int a2 = (int) Math.round(gear2position * TurretConstants.gear2Size);

        //Calculating M, M1, M2, and their inverses.
        int M = m1*m2;
        int M1 = M/m1;
        int M2 = M/m2;

        int inverseM1 = 0;
        int inverseM2 = 0;

        //Going to m1 because the lowest possible value of inverseM1 to get a remainder of 1 is going to be less than m1.
        for (var i = 0; i <= m1; i++) {
            if ((M1 *i) % m1 == 1) {
                inverseM1 = i;
                break;
            }
        }

        for (var i = 0; i <= m2; i++) {
            if ((M2 *i) % m2 == 1) {
                inverseM2 = i;
                break;
            }
        }
        double turretPositionInTeeth = (a1*M1*inverseM1 + a2*M2*inverseM2)%M;

        if (turretPositionInTeeth > TurretConstants.turretSize) {
            throw new IllegalArgumentException("Current turret encoders reflect value over possible turret positions.");
        }

        double turretPositionInRadians = (turretPositionInTeeth * 2 * Math.PI / TurretConstants.turretSize) - Math.PI;

        return turretPositionInRadians;
    }

}
