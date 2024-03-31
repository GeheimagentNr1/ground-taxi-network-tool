package de.secretsoft.ground_taxi_network_tool.job.steps.print_errors;

import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class PrintErrorsStepConfiguration {
	
	
	private static final String STEP_NAME = "printErrorsStep";
	
	@Bean
	public Step printErrorsStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		PrintErrorsTasklet printErrorsTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( printErrorsTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public PrintErrorsTasklet printErrorsTasklet(
		final RoutesHolder routesHolder ) {
		
		return new PrintErrorsTasklet(
			routesHolder
		);
	}
}
