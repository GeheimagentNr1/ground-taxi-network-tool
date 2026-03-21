package de.secretsoft.ground_taxi_network_tool.job.ground_layout_to_kml.steps.read_ground_layout;

import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ReadGroundLayoutStepConfiguration {
	
	
	private static final String STEP_NAME = "readGroundLayoutStep";
	
	@Bean
	public Step readGroundLayoutStep(
		JobRepository jobRepository,
		ReadGroundLayoutTasklet readGroundLayoutTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( readGroundLayoutTasklet )
			.build();
	}
	
	@Bean
	@StepScope
	public ReadGroundLayoutTasklet readGroundLayoutTasklet(
		final RoutesHolder routesHolder ) {
		
		return new ReadGroundLayoutTasklet(
			routesHolder
		);
	}
}
