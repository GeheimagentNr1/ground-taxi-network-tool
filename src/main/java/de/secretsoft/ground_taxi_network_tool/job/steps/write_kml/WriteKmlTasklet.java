package de.secretsoft.ground_taxi_network_tool.job.steps.write_kml;

import de.secretsoft.ground_taxi_network_tool.config.GroundTaxiNetworkToolConfig;
import de.secretsoft.ground_taxi_network_tool.services.KmlWriter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import net.opengis.kml._2.Document;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.IOException;


@RequiredArgsConstructor
public class WriteKmlTasklet implements Tasklet {
	
	
	private final GroundTaxiNetworkToolConfig groundTaxiNetworkToolConfig;
	
	private final RoutesHolder routesHolder;
	
	private final KmlWriter kmlWriter;
	
	private final StaxEventItemWriter<Document> writeKmlWriter;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) throws IOException {
		
		writeKmlWriter.open( contribution.getStepExecution().getExecutionContext() );
		writeKmlWriter.write( Chunk.of(
			kmlWriter.routesToKmlDocument(
				groundTaxiNetworkToolConfig.getProcessedKmlFile().getFileName().toString(),
				routesHolder.getRoutes()
			)
		) );
		writeKmlWriter.close();
		return RepeatStatus.FINISHED;
	}
}
