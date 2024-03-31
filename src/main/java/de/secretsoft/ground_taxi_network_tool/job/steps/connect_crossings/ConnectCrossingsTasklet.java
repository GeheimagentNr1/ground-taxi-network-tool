package de.secretsoft.ground_taxi_network_tool.job.steps.connect_crossings;

import de.secretsoft.ground_taxi_network_tool.services.CrossingConnector;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class ConnectCrossingsTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final CrossingConnector crossingConnector;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		routesHolder.addErrors(
			crossingConnector.connectCrossings( routesHolder.getRoutes() )
		);
		return RepeatStatus.FINISHED;
	}
}
