
package frc.robot.subsystems.indexer;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
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

  public Command intake() {
    return Commands.runOnce(() -> io.setVoltage(IndexerConstants.kIntakeVoltage));
  }

  public Command eject() {
    return Commands.runOnce(() -> io.setVoltage(IndexerConstants.kEjectVoltage));
  }

  public Command stop() {
    return Commands.runOnce(() -> io.stop());
  }

}
