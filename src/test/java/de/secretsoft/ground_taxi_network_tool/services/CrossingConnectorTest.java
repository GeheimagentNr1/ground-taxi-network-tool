package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


class CrossingConnectorTest {
	
	
	private final CrossingConnector crossingConnector = new CrossingConnector();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/crossing-connector-test-data.json" )
	@ParameterizedTest
	void connectCrossings(
		@Property( "displayName" ) String displayName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedRoutes" ) List<RouteData> expectedRoutes,
		@Property( "errors" ) List<String> errors ) {
		
		Set<Double> latitude = new TreeSet<>();
		Set<Double> longitude = new TreeSet<>();
		inputRoutes.stream()
			.filter( routeData -> !( routeData instanceof Stand ) && !( routeData instanceof Taxiout ) )
			.forEach( routeData -> routeData.getPoints().stream()
				.filter( PointData::hasCrossingName )
				.forEach( pointData -> {
					if( routeData instanceof HoldingPoint ) {
						Assertions.assertTrue(
							latitude.contains( pointData.getLatitude() ),
							"double latitude: " + pointData.getLatitude()
						);
						Assertions.assertTrue(
							longitude.contains( pointData.getLongitude() ),
							"double longitude: " + pointData.getLongitude()
						);
					} else {
						Assertions.assertFalse(
							latitude.contains( pointData.getLatitude() ),
							"double latitude: " + pointData.getLatitude()
						);
						Assertions.assertFalse(
							longitude.contains( pointData.getLongitude() ),
							"double longitude: " + pointData.getLongitude()
						);
					}
					latitude.add( pointData.getLatitude() );
					longitude.add( pointData.getLongitude() );
				} ) );
		Assertions.assertEquals(
			errors,
			crossingConnector.connectCrossings( inputRoutes )
		);
		Assertions.assertIterableEquals(
			expectedRoutes,
			inputRoutes
		);
	}
}