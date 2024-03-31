package de.secretsoft.ground_taxi_network_tool.job.steps.add_gate_and_taxiout_points;

import de.secretsoft.ground_taxi_network_tool.services.GateAndTaxioutPointAdder;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class AddGateAndTaxioutPointsStepConfiguration {
	
	
	private static final String STEP_NAME = "addGateAndTaxioutPointsStep";
	
	@Bean
	public Step addGateAndTaxioutPointsStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		AddGateAndTaxioutPointsTasklet addGateAndTaxioutPointsTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( addGateAndTaxioutPointsTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public AddGateAndTaxioutPointsTasklet addGateAndTaxioutPointsTasklet(
		final RoutesHolder routesHolder,
		final GateAndTaxioutPointAdder gateAndTaxioutPointAdder ) {
		
		return new AddGateAndTaxioutPointsTasklet(
			routesHolder,
			gateAndTaxioutPointAdder
		);
	}
}
