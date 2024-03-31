package de.secretsoft.ground_taxi_network_tool.job.steps.write_kml;

import de.secretsoft.ground_taxi_network_tool.config.GroundTaxiNetworkToolConfig;
import de.secretsoft.ground_taxi_network_tool.services.KmlWriter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import net.opengis.kml._2.Document;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class WriteKmlStepConfiguration {
	
	
	private static final String STEP_NAME = "writeKmlStep";
	
	private static final String WRITER_NAME = "writeKmlWriter";
	
	@Bean
	public Step writeKmlStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		WriteKmlTasklet writeKmlTasklet
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.tasklet( writeKmlTasklet, transactionManager )
			.build();
	}
	
	@Bean
	@StepScope
	public WriteKmlTasklet writeKmlTasklet(
		final GroundTaxiNetworkToolConfig groundTaxiNetworkToolConfig,
		final RoutesHolder routesHolder,
		final KmlWriter kmlWriter,
		final StaxEventItemWriter<Document> writeKmlWriter ) {
		
		return new WriteKmlTasklet(
			groundTaxiNetworkToolConfig,
			routesHolder,
			kmlWriter,
			writeKmlWriter
		);
	}
	
	@Bean
	@StepScope
	public StaxEventItemWriter<Document> writeKmlWriter(
		@Qualifier( "kmlOutputResource" ) final WritableResource kmlOutputResource ) {
		
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound( Document.class );
		return new StaxEventItemWriterBuilder<Document>()
			.name( WRITER_NAME )
			.rootTagName( "kml" )
			.marshaller( marshaller )
			.resource( kmlOutputResource )
			.saveState( false )
			.build();
	}
	
	@Bean
	@StepScope
	public WritableResource kmlOutputResource( final GroundTaxiNetworkToolConfig groundTaxiNetworkToolConfig ) {
		
		return new FileSystemResource( groundTaxiNetworkToolConfig.getProcessedKmlFile() );
	}
}
