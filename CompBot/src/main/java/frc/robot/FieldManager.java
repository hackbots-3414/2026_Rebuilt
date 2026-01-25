package frc.robot;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.StructArrayLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class FieldManager {
  private static FieldManager instance;

  private Field2d field;
  private List<Pose3d> fuel = new ArrayList<>(20);
  private List<Pose2d> fuel2d = new ArrayList<>(20);

  // logging
  private final DataLog dataLog;
  private final StructArrayLogEntry<Pose3d> fuelEntries;

  private FieldManager() {
    dataLog = DataLogManager.getLog();
    fuelEntries = StructArrayLogEntry.create(dataLog, "fuels", Pose3d.struct);
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

  public void addFuel(Pose3d pose) {
    fuel.add(pose);
    fuel2d.add(pose.toPose2d());
  }

  public void clearFuel() {
    fuel.clear();
    fuel2d.clear();
  }

  public void drawFuel() {
    field.getObject("FUEL").setPoses(fuel2d);
    fuelEntries.update(fuel);
  }
}
