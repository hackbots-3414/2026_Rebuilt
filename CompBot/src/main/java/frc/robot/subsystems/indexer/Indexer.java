
package frc.robot.subsystems.indexer;

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
    return this.run(() -> io.setFeedVoltage(FeederConstants.kIndexVoltage)).finallyDo (() -> io.stop());
  }

  public Command eject() {
    return this.run(() -> io.setFeedVoltage(FeederConstants.kEjectVoltage)).finallyDo (() -> io.stop());
  }

  public Command stop() {
    return this.run(() -> io.stop()).finallyDo (() -> io.stop());
  }

}
