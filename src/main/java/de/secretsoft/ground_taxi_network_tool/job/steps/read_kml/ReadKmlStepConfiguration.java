package de.secretsoft.ground_taxi_network_tool.job.steps.read_kml;

import de.secretsoft.ground_taxi_network_tool.config.GroundTaxiNetworkToolConfig;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.services.KmlLoader;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import net.opengis.kml._2.Placemark;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ReadKmlStepConfiguration {
	
	
	private static final String STEP_NAME = "readKmlStep";
	
	private static final String READER_NAME = "readKmlReader";
	
	@Bean
	public Step readKmlStep(
		final JobRepository jobRepository,
		final PlatformTransactionManager transactionManager,
		final StaxEventItemReader<Placemark> kmlReader,
		final ReadKmlProcessor readKmlProcessor,
		final ReadKmlWriter readKmlWriter
	) {
		
		return new StepBuilder( STEP_NAME, jobRepository )
			.<Placemark, RouteData> chunk( 1000, transactionManager )
			.reader( kmlReader )
			.processor( readKmlProcessor )
			.writer( readKmlWriter )
			.build();
	}
	
	@Bean
	@StepScope
	public StaxEventItemReader<Placemark> readKmlReader(
		@Qualifier( "inputKmlResource" ) final Resource inputKmlResource ) {
		
		//XStreamMarshaller marshaller = new XStreamMarshaller();
		//marshaller.setPackagesToScan( "de.secretsoft.ground_taxi_network_tool.models.kml" );
		//marshaller.setAutodetectAnnotations( true );
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound( Placemark.class );
		
		//http://earth.google.com/kml/2.1
		return new StaxEventItemReaderBuilder<Placemark>()
			.name( READER_NAME )
			.addFragmentRootElements( "{http://www.opengis.net/kml/2.2}Placemark" )
			.unmarshaller( marshaller )
			.resource( inputKmlResource )
			.saveState( false )
			.build();
	}
	
	@Bean
	@StepScope
	public Resource inputKmlResource( final GroundTaxiNetworkToolConfig groundTaxiNetworkToolConfig ) {
		
		return new FileSystemResource( groundTaxiNetworkToolConfig.getInputKmlFile() );
	}
	
	@Bean
	@StepScope
	public ReadKmlProcessor readKmlProcessor(
		final RoutesHolder routesHolder,
		final KmlLoader kmlLoader ) {
		
		return new ReadKmlProcessor( routesHolder, kmlLoader );
	}
	
	@Bean
	@StepScope
	public ReadKmlWriter readKmlWriter( final RoutesHolder routesHolder ) {
		
		return new ReadKmlWriter( routesHolder );
	}
}
