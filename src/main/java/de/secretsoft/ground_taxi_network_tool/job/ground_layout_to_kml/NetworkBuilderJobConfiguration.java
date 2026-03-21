package de.secretsoft.ground_taxi_network_tool.job.ground_layout_to_kml;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@Configuration
public class NetworkBuilderJobConfiguration {
	
	
	private static final String JOB_NAME = "networkBuilderJob";
	
	@Bean
	public Job networkBuilderJob(
		final JobRepository jobRepository,
		@Qualifier( "readGroundLayoutStep" ) final Step readGroundLayoutStep,
		@Qualifier( "writeKmlStep" ) final Step writeKmlStep ) {
		
		return new JobBuilder( JOB_NAME, jobRepository )
			.start( readGroundLayoutStep )
			.next( writeKmlStep )
			.build();
	}
}
