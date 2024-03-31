package de.secretsoft.ground_taxi_network_tool.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NetworkBuilderJobConfiguration {
	
	
	private static final String JOB_NAME = "networkBuilderJob";
	
	@Bean
	public Job networkBuilderJob(
		final JobRepository jobRepository,
		@Qualifier( "readKmlStep" ) final Step readKmlStep,
		@Qualifier( "buildExitsStep" ) final Step buildExitsStep,
		@Qualifier( "addGateAndTaxioutPointsStep" ) final Step addGateAndTaxioutPointsStep,
		@Qualifier( "connectGatesAndTaxioutsStep" ) final Step connectGatesAndTaxioutsStep,
		@Qualifier( "connectCrossingsStep" ) final Step connectCrossingsStep,
		@Qualifier( "sortCoordinatesStep" ) final Step sortCoordinatesStep,
		@Qualifier( "printErrorsStep" ) final Step printErrorsStep,
		@Qualifier( "writeKmlStep" ) final Step writeKmlStep,
		@Qualifier( "writeGroundLayoutStep" ) final Step writeGroundLayoutStep ) {
		
		return new JobBuilder( JOB_NAME, jobRepository )
			.start( readKmlStep )
			.next( buildExitsStep )
			.next( addGateAndTaxioutPointsStep )
			.next( connectGatesAndTaxioutsStep )
			.next( connectCrossingsStep )
			.next( sortCoordinatesStep )
			.next( printErrorsStep )
			.next( writeKmlStep )
			.next( writeGroundLayoutStep )
			.build();
	}
}
