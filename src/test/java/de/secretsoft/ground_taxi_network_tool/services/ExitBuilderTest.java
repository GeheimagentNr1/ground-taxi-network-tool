package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class ExitBuilderTest {
	
	
	private final ExitBuilder exitBuilder = new ExitBuilder();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/exit-builder-test-data.json" )
	@ParameterizedTest
	void processExit(
		@Property( "displayName" ) String displayName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedRoutes" ) List<RouteData> expectedRoutes,
		@Property( "errors" ) List<String> errors ) {
		
		Assertions.assertEquals(
			errors,
			exitBuilder.processExits( inputRoutes )
		);
		Assertions.assertIterableEquals(
			expectedRoutes,
			inputRoutes
		);
	}
}