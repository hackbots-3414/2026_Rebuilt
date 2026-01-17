package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FieldManager {
    private static FieldManager instance;

    private Field2d field;

    private FieldManager() {
        setField(new Field2d());
    }

    public static FieldManager getInstance() {
        if (instance == null) {
            instance = new FieldManager();
        }
        return instance;
    }

    public void setField(Field2d field) {
        this.field = field;
        SmartDashboard.putData("Robot/Field", field);
    }

    public Field2d getField() {
        return field;
    }
}
