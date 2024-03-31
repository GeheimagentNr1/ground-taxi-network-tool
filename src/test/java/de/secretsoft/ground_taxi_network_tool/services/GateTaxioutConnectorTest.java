package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.models.Stand;
import de.secretsoft.ground_taxi_network_tool.models.Taxiout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


class GateTaxioutConnectorTest {
	
	
	private final GateTaxioutConnector gateTaxioutConnector = new GateTaxioutConnector();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/gate-taxiout-connector-test-data.json" )
	@ParameterizedTest
	void connectGatesAndTaxiouts(
		@Property( "displayName" ) String displayName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedRoutes" ) List<RouteData> expectedRoutes,
		@Property( "errors" ) List<String> errors ) {
		
		Set<Double> latitude = new TreeSet<>();
		Set<Double> longitude = new TreeSet<>();
		inputRoutes.stream()
			.filter( routeData -> routeData instanceof Stand || routeData instanceof Taxiout )
			.flatMap( routeData -> routeData.getPoints().stream() )
			.filter( PointData::hasCrossingName )
			.forEach( pointData -> {
				Assertions.assertFalse(
					latitude.contains( pointData.getLatitude() ),
					"double latitude: " + pointData.getLatitude()
				);
				Assertions.assertFalse(
					longitude.contains( pointData.getLongitude() ),
					"double longitude: " + pointData.getLongitude()
				);
				latitude.add( pointData.getLatitude() );
				longitude.add( pointData.getLongitude() );
			} );
		Assertions.assertEquals(
			errors,
			gateTaxioutConnector.connectGatesAndTaxiouts( inputRoutes )
		);
		Assertions.assertIterableEquals(
			expectedRoutes,
			inputRoutes
		);
	}
}