package frc.robot.subsystems.Turret;

import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.aiming.AimParams;
import frc.robot.subsystems.Turret.TurretIO.TurretIOInputs;
import frc.robot.superstructure.StateManager;

public class Turret extends SubsystemBase {

  private final TurretIO io;
  private final TurretIOInputs inputs;

  private final Alert calibrationAlert =
      new Alert("Turret not calibrated successfully", AlertType.kError);

  public Turret(TurretIO io) {
    super();
    this.io = io;
    inputs = new TurretIOInputs();
    io.calibrate();
    SmartDashboard.putData("Turret/home", home());
    SmartDashboard.putData("Turret/Calibrate", runOnce(io::calibrate));
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    calibrationAlert.set(!inputs.calibrated);
  }

  public Command track(StateManager configuration, Supplier<AimParams> aimParams) {
    return this.run(() -> {
      Rotation2d robot = configuration.robotPose().getRotation();
      Rotation2d relative = aimParams.get().yaw.minus(robot);
      io.setPosition(relative.getMeasure());
    });
  }

  /**
   * Sends the turret to its home position.
   */
  public Command home() {
    return Commands.sequence(
        runOnce(() -> io.setPosition(TurretConstants.kHomePosition)),
        Commands.waitUntil(ready()));
  }

  public Trigger ready() {
    return new Trigger(() -> inputs.position.minus(inputs.reference)
        .baseUnitMagnitude() <= TurretConstants.kTolerance.baseUnitMagnitude());
  }
}
