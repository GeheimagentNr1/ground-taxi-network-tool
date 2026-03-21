package de.secretsoft.ground_taxi_network_tool.job.steps.print_errors;

import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;


@Slf4j
@RequiredArgsConstructor
public class PrintErrorsTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		for( String error : routesHolder.getErrors() ) {
			log.error( error );
		}
		return RepeatStatus.FINISHED;
	}
}
