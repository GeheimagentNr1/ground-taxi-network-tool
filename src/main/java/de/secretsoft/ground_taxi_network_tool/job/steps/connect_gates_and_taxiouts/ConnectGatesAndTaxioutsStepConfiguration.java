package de.secretsoft.ground_taxi_network_tool.job.steps.connect_gates_and_taxiouts;

import de.secretsoft.ground_taxi_network_tool.services.GateTaxioutConnector;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ConnectGatesAndTaxioutsStepConfiguration {
	
	
	private static final String STEP_NAME = "connectGatesAndTaxioutsStep";
	
	@Bean
	public Step connectGatesAndTaxioutsStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		ConnectGatesAndTaxioutsTasklet connectGatesAndTaxioutsTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( connectGatesAndTaxioutsTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public ConnectGatesAndTaxioutsTasklet connectGatesAndTaxioutsTasklet(
		final RoutesHolder routesHolder,
		final GateTaxioutConnector gateTaxioutConnector ) {
		
		return new ConnectGatesAndTaxioutsTasklet(
			routesHolder,
			gateTaxioutConnector
		);
	}
}
