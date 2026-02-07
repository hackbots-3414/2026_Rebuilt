
package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;
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
          io.setFeedVoltage(FeederConstants.kIndexVoltage);
          io.setSpindexerVoltage(SpindexerConstants.kSpinVoltage);
        },
        this::stop);
  }

  /**
   * Returns a command that runs the feeder in reverse and stops the spindexer. This can be used to
   * unjam the shooter/feeder system.
   */
  public Command eject() {
    return this.startEnd(
        () -> {
          // The spindexer can't really run in reverse, so we just tell it to temporarily stop.
          io.setSpindexerVoltage(Volts.zero());
          io.setFeedVoltage(FeederConstants.kEjectVoltage);
        },
        this::stop);
  }

  private void stop() {
    io.setFeedVoltage(Volts.zero());
    io.setSpindexerVoltage(Volts.zero());
  }
}
