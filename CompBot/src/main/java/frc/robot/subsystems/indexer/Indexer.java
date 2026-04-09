
package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.indexer.IndexerConstants.FeederConstants;
import frc.robot.subsystems.indexer.IndexerConstants.SpindexerConstants;
import frc.robot.subsystems.indexer.IndexerIO.IndexerIOInputs;

public class Indexer extends SubsystemBase {
  private final IndexerIO io;
  private final IndexerIOInputs inputs = new IndexerIOInputs();

  public Indexer(IndexerIO io) {
    this.io = io;
    SmartDashboard.putData("Spindexer/Index with variation", indexWithVariation());
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  /**
   * Returns a command that runs the indexer system to move fuel from the hopper into the shooter.
   */
  public Command index() {
    return this.startEnd(
        () -> {
          io.setFeedVoltage(FeederConstants.kFeedVoltage);
          io.setSpindexerVoltage(SpindexerConstants.kSpinVoltage);
        },
        this::stop);
  }

  public Command indexWithVariation() {
    final double MIN_SPIN_VOLTAGE = 9.0;
    final double MAX_SPIN_VOLTAGE = 12.0;
    final double OMEGA = Math.PI; // Make this faster for more variation
    return this.runEnd(
        () -> {
          io.setFeedVoltage(FeederConstants.kFeedVoltage);
          // Ensure always positive, and in the range from 0 to 1.
          double phase = 0.5 * (Math.sin(Timer.getTimestamp() * OMEGA) + 1.0);
          double output = MathUtil.interpolate(MIN_SPIN_VOLTAGE, MAX_SPIN_VOLTAGE, phase);
          io.setSpindexerVoltage(Volts.of(output));
        },
        this::stop);
  }

  private void stop() {
    io.setFeedVoltage(Volts.zero());
    io.setSpindexerVoltage(Volts.zero());
  }
}
