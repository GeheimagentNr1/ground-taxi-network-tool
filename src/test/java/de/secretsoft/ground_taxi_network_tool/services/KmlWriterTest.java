package de.secretsoft.ground_taxi_network_tool.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import net.opengis.kml._2.Document;
import net.opengis.kml._2.Placemark;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class KmlWriterTest {
	
	
	private final ObjectMapper objectMapper = new ObjectMapper().enable( SerializationFeature.INDENT_OUTPUT );
	
	private final KmlWriter kmlWriter = new KmlWriter();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/kml-writer-test-data.json" )
	@ParameterizedTest
	void routesToKmlDocument(
		@Property( "displayName" ) String displayName,
		@Property( "inputDocumentName" ) String inputDocumentName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedDocument" ) Document expectedDocument ) throws JsonProcessingException {
		
		Document actualDocument = kmlWriter.routesToKmlDocument( inputDocumentName, inputRoutes );
		Assertions.assertEquals(
			objectMapper.writeValueAsString( expectedDocument ),
			objectMapper.writeValueAsString( actualDocument )
		);
		Assertions.assertEquals( expectedDocument.getName(), actualDocument.getName() );
		Assertions.assertEquals( expectedDocument.getPlacemarks().size(), actualDocument.getPlacemarks().size() );
		for( int i = 0; i < expectedDocument.getPlacemarks().size(); i++ ) {
			Placemark expectedPlacemark = expectedDocument.getPlacemarks().get( i );
			Placemark actualPlacemark = actualDocument.getPlacemarks().get( i );
			Assertions.assertEquals( expectedPlacemark.getName(), actualPlacemark.getName() );
			Assertions.assertIterableEquals(
				expectedPlacemark.getLineString().getCoordinates(),
				actualPlacemark.getLineString().getCoordinates()
			);
		}
	}
}