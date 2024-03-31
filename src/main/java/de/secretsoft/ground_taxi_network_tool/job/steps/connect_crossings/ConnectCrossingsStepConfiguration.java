package de.secretsoft.ground_taxi_network_tool.job.steps.connect_crossings;

import de.secretsoft.ground_taxi_network_tool.services.CrossingConnector;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ConnectCrossingsStepConfiguration {
	
	
	private static final String STEP_NAME = "connectCrossingsStep";
	
	@Bean
	public Step connectCrossingsStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		ConnectCrossingsTasklet connectCrossingsTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( connectCrossingsTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public ConnectCrossingsTasklet connectCrossingsTasklet(
		final RoutesHolder routesHolder,
		final CrossingConnector crossingConnector ) {
		
		return new ConnectCrossingsTasklet(
			routesHolder,
			crossingConnector
		);
	}
}
