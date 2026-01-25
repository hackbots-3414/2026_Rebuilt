package frc.robot.subsystems.turret;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import frc.robot.subsystems.Turret.TurretIOHardware;
import frc.robot.subsystems.Turret.TurretConstants.TurretCRTConstants;

public class CRTTest {
    private static final double EPSILON = 1e-3;

    @Test
    public void testProblemSpace() {
        // for (double i = 0;i < 1.0;i += 0.001) {
        //     testSpecificCRT(i);
        // }
    }

    private void testSpecificCRT(double x100) {
        double calculated = TurretIOHardware.crtSolve(x12(x100), x26(x100));
        double delta = Math.abs(calculated - x100);
        assertTrue(delta <= EPSILON);
    }

    private double x12(double x100) {
        double converted = x100 / TurretCRTConstants.kG12;
        while (converted > 1.0) converted --;
        return converted;
    }

    private double x26(double x100) {
        double converted = x100 / TurretCRTConstants.kG26;
        while (converted > 1.0) converted --;
        return converted;
    }
}
