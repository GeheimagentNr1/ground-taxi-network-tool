package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class GateAndTaxioutPointAdderTest {
	
	
	private final GateAndTaxioutPointAdder gateAndTaxioutPointAdder = new GateAndTaxioutPointAdder();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/gate-and-taxiout-point-adder-test-data.json" )
	@ParameterizedTest
	void addGatePoints(
		@Property( "displayName" ) String displayName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedRoutes" ) List<RouteData> expectedRoutes,
		@Property( "errors" ) List<String> errors ) {
		
		Assertions.assertIterableEquals(
			errors,
			gateAndTaxioutPointAdder.addGatePoints( inputRoutes )
		);
		Assertions.assertIterableEquals(
			expectedRoutes,
			inputRoutes
		);
	}
}