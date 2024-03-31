package de.secretsoft.ground_taxi_network_tool.job.steps.sort_coordinates;

import de.secretsoft.ground_taxi_network_tool.services.CoordinatesSorter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class SortCoordinatesTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final CoordinatesSorter coordinatesSorter;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		coordinatesSorter.sort( routesHolder.getRoutes() );
		return RepeatStatus.FINISHED;
	}
}
