package de.secretsoft.ground_taxi_network_tool.job.steps.write_ground_layout;

import de.secretsoft.ground_taxi_network_tool.config.GroundTaxiNetworkToolConfig;
import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class WriteGroundLayoutStepConfiguration {
	
	
	private static final String STEP_NAME = "writeGroundLayoutStep";
	
	private static final String WRITER_NAME = "writeGroundLayoutWriter";
	
	@Bean
	public Step writeGroundLayoutStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		WriteGroundLayoutTasklet writeGroundLayoutTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( writeGroundLayoutTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public WriteGroundLayoutTasklet writeGroundLayoutTasklet(
		final RoutesHolder routesHolder,
		final GroundLayoutFileItemWriter writeGroundLayoutWriter ) {
		
		return new WriteGroundLayoutTasklet(
			routesHolder,
			writeGroundLayoutWriter
		);
	}
	
	@Bean
	@StepScope
	public GroundLayoutFileItemWriter writeGroundLayoutWriter(
		@Qualifier( "groundLayoutOutputResource" ) final WritableResource groundLayoutOutputResource,
		final LatLngConverter latLngConverter ) {
		
		GroundLayoutFileItemWriter groundLayoutFileItemWriter = new GroundLayoutFileItemWriter( latLngConverter );
		groundLayoutFileItemWriter.setName( WRITER_NAME );
		groundLayoutFileItemWriter.setResource( groundLayoutOutputResource );
		return groundLayoutFileItemWriter;
	}
	
	@Bean
	@StepScope
	public WritableResource groundLayoutOutputResource( final GroundTaxiNetworkToolConfig groundTaxiNetworkToolConfig ) {
		
		return new FileSystemResource( groundTaxiNetworkToolConfig.getGroundLayoutFile() );
	}
}
