package de.secretsoft.ground_taxi_network_tool.job.steps.build_exits;

import de.secretsoft.ground_taxi_network_tool.services.ExitBuilder;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class BuildExitsTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final ExitBuilder exitBuilder;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		routesHolder.addErrors(
			exitBuilder.processExits( routesHolder.getRoutes() )
		);
		return RepeatStatus.FINISHED;
	}
}
