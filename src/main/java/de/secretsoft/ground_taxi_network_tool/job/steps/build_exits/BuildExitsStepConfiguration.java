package de.secretsoft.ground_taxi_network_tool.job.steps.build_exits;

import de.secretsoft.ground_taxi_network_tool.services.ExitBuilder;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class BuildExitsStepConfiguration {
	
	
	private static final String STEP_NAME = "buildExitsStep";
	
	@Bean
	public Step buildExitsStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		BuildExitsTasklet buildExitsTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( buildExitsTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public BuildExitsTasklet buildExitsTasklet(
		final RoutesHolder routesHolder,
		final ExitBuilder exitBuilder ) {
		
		return new BuildExitsTasklet(
			routesHolder,
			exitBuilder
		);
	}
}
