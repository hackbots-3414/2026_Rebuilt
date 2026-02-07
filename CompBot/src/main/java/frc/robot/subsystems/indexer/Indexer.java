
package frc.robot.subsystems.indexer;

import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.indexer.IndexerConstants.FeederConstants;
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

  public Command index() {
    return this.startEnd(
        () -> io.setFeedVoltage(FeederConstants.kIndexVoltage),
        this::stop);
  }

  public Command eject() {
    return this.startEnd(
        () -> io.setFeedVoltage(FeederConstants.kEjectVoltage),
        this::stop);
  }

  private void stop() {
    io.setFeedVoltage(Volts.zero());
    io.setSpindexerVoltage(Volts.zero());
  }
}
