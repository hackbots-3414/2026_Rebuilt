package frc.robot.binding;

public class BindingConstants {
    public static class Driver {
        /*
         * Button ID | PS5 Button
         * 
         * 1 - Square
         * 2 - Cross
         * 3 - Circle
         * 4 - Triangle
         * 5 - L1
         * 6 - R1
         * 7 - L2
         * 8 - R2
         * 9 - Three weird lines (I think it's "Create")
         * 10 - Menu
         * 11 - Left joystick push
         * 12 - Right joystick push
         * 13 - PS button
         * 14 - Touchpad
         * 15 - Weird little bar button
         */

        public static final int kDriveControllerPort = 0;

        /**
         * PLEASE NOTE:
         * X axis refers to robot/field X, not controller X. Same for Y.
         */
        public static final int kXAxis = 1;
        public static final int kYAxis = 0;
        public static final int kRotAxis = 4;

        public static final boolean kFlipX = true;
        public static final boolean kFlipY = true;
        public static final boolean kFlipRot = true;
    }

    public static class Operator {
        public static final int kOperatorControllerPort = 2;
    }
}
