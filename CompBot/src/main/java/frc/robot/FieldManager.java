package frc.robot;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FieldManager {
    private static FieldManager instance;

    private Field2d field;
    private List<Pose2d> fuel = new ArrayList<>(20);

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

    public void addFuel(Pose2d pose) {
      fuel.add(pose);
    }

    public void clearFuel() {
      fuel.clear();
    }

    public void drawFuel() {
      field.getObject("FUEL").setPoses(fuel);
    }
}
