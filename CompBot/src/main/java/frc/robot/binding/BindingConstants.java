package frc.robot.binding;

public class BindingConstants {
    public static class Driver {
        public static final int kDriveControllerPort = 0;

        /* These values may not be correct. Test with chosen controller. */
        public static final int kXAxis = 1;
        public static final int kYAxis = 0;
        public static final int kRotAxis = 3;

        public static final boolean kFlipX = false;
        public static final boolean kFlipY = true;
        public static final boolean kFlipRot = false;
    }

    public static class Operator {
        public static final int kOperatorControllerPort = 2;
    }
}
