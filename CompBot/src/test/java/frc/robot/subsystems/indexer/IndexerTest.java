package frc.robot.subsystems.indexer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static edu.wpi.first.units.Units.Volts;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.CommandBasedTest;
import frc.robot.subsystems.indexer.IndexerConstants.FeederConstants;
import frc.robot.subsystems.indexer.IndexerConstants.SpindexerConstants;
import frc.robot.subsystems.indexer.IndexerIO.IndexerIOInputs;

public class IndexerTest extends CommandBasedTest {
    @Test
    public void indexerTest() {
        IndexerIO mockIndexerIO = mock(IndexerIO.class);

        Indexer indexer = new Indexer(mockIndexerIO);
        CommandScheduler.getInstance().run();
        // Have to instantiate the Inputs
        verify(mockIndexerIO).updateInputs(Mockito.any(IndexerIOInputs.class));

        CommandScheduler.getInstance().schedule(indexer.index());
        CommandScheduler.getInstance().run();
        verify(mockIndexerIO).setFeedVoltage(FeederConstants.kIndexVoltage);
        verify(mockIndexerIO).setSpindexerVoltage(SpindexerConstants.kSpinVoltage);
    }

    @Test 
    public void ejecterTest() {
        IndexerIO mockIndexerIO = mock(IndexerIO.class);
        Indexer indexer =  new Indexer(mockIndexerIO);
        
        CommandScheduler.getInstance().schedule(indexer.eject());
        CommandScheduler.getInstance().run();
        verify(mockIndexerIO).setSpindexerVoltage(Volts.zero());
        verify(mockIndexerIO).setFeedVoltage(FeederConstants.kEjectVoltage);
    }

}