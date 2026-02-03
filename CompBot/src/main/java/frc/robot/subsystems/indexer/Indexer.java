
package frc.robot.subsystems.indexer;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
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
    return this.run(() -> io.setVoltage(IndexerConstants.kIndexVoltage)).finallyDo (() -> io.stop());
  }

  public Command eject() {
    return this.run(() -> io.setVoltage(IndexerConstants.kEjectVoltage)).finallyDo (() -> io.stop());
  }

  public Command stop() {
    return this.run(() -> io.stop()).finallyDo (() -> io.stop());
  }

}
