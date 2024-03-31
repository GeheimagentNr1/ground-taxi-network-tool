package de.secretsoft.ground_taxi_network_tool.job.steps.sort_coordinates;

import de.secretsoft.ground_taxi_network_tool.services.CoordinatesSorter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class SortCoordinatesStepConfiguration {
	
	
	private static final String STEP_NAME = "sortCoordinatesStep";
	
	@Bean
	public Step sortCoordinatesStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		SortCoordinatesTasklet sortCoordinatesTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( sortCoordinatesTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public SortCoordinatesTasklet sortCoordinatesTasklet(
		final RoutesHolder routesHolder,
		final CoordinatesSorter coordinatesSorter ) {
		
		return new SortCoordinatesTasklet(
			routesHolder,
			coordinatesSorter
		);
	}
}
