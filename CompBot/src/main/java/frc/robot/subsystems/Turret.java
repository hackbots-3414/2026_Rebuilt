package frc.robot.subsystems;

public class Turret {

    private double getTurretLocation(double gear1position, double gear2position) {
        
        //These can be constants in Constants.java - Gear tooth numbers. Not coprime, which may invalidate the theorem.
        int gear1size = 28;
        int gear2size = 26;
        int turretSize = 100;

        //Changing gear names to match video so we can follow the video
        int m1 = gear1size;
        int m2 = gear2size;

        //Turn CANcoder's -1 to 1 measurement into teeth.
        int a1 = (int) Math.round((gear1position+1) * gear1size/2);
        int a2 = (int) Math.round((gear2position+1) * gear2size/2);

        //Calculating M, M1, M2, and their inverses.
        int M = m1*m2;
        int M1 = M/m1;
        int M2 = M/m2;

        int inverseM1 = 0;
        int inverseM2 = 0;

        //Going to m1 because it has to be less than that in order to get a remainder of 1.
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
        return (a1*M1*inverseM1 + a2*M2*inverseM2)%M;
    }
    
}
