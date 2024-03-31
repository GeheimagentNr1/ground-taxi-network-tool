package de.secretsoft.ground_taxi_network_tool.job.steps.write_ground_layout;

import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class WriteGroundLayoutTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final GroundLayoutFileItemWriter writeGroundLayoutWriter;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) throws Exception {
		
		writeGroundLayoutWriter.open( contribution.getStepExecution().getExecutionContext() );
		writeGroundLayoutWriter.write( new Chunk<>( routesHolder.getRoutes() ) );
		writeGroundLayoutWriter.close();
		return RepeatStatus.FINISHED;
	}
}
