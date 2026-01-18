package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.FieldManager;
import frc.robot.aiming.AimParams;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

public class FuelShotSim implements CommandBuilder {
  public Command singleBuild(Subsystems subsystems, StateManager state) {
    FuelSim sim = new FuelSim();
    return Commands.sequence(
        Commands.runOnce(() -> {
          sim.launch(state, subsystems.drivetrain().aimParams().get());
        }),
        Commands.run(sim::tick))
        .until(sim::atHub)
        .onlyIf(() -> subsystems.drivetrain().aimParams().isPresent())
        .withName("Fuel Shot (sim)");
  }

  public Command build(Subsystems subsystems, StateManager state) {
    return Commands.runOnce(() -> CommandScheduler.getInstance().schedule(singleBuild(subsystems, state)));
  }

  public class FuelSim {
    private static final double dt = 0.02;
    private static final int resolution = 10;

    private Translation3d position;
    private Translation3d velocity;
    private Translation3d target;

    private Translation3d gravity = new Translation3d(0, 0, -9.81);

    public FuelSim() {}

    public void launch(StateManager state, AimParams params) {
      position = new Translation3d(state.robotPose().getTranslation());
      // field-relative velocity, but with the robot as the origin
      Translation3d veloR = new Translation3d(
          params.pitch.getCos() * params.yaw.getCos() * params.velocity.baseUnitMagnitude(),
          params.pitch.getCos() * params.yaw.getSin() * params.velocity.baseUnitMagnitude(),
          params.pitch.getSin() * params.velocity.baseUnitMagnitude());
      velocity = veloR.plus(new Translation3d(state.robotVelocity().getTranslation()));
      target = state.aimTarget().getTranslation();
    }

    public void tick() {
      for (int i = 0;i < resolution;i ++) {
        // Update position
        position = position.plus(velocity.times(dt / (double)resolution));
        // Update velocity
        // Calculate the drag force / mass: 0.5 * 1.225 * pi * 0.075^2 * 0.47 * v^2 * 2
        double Fd = -0.5 * 1.225 * Math.PI * 0.075 * 0.075 * 0.47 * velocity.getNorm() * 2;
        Translation3d acceleration = velocity.times(Fd).plus(gravity);
        velocity = velocity.plus(acceleration.times(dt / (double)resolution));
      }
      FieldManager.getInstance().addFuel(new Pose3d(position, Rotation3d.kZero));
    }

    public boolean atHub() {
      return position.getZ() < target.getZ() && velocity.getZ() < 0;
    }

  }
}
