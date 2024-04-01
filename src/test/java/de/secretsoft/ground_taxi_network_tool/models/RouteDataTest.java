package de.secretsoft.ground_taxi_network_tool.models;

import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class RouteDataTest {
	
	
	private final LatLngConverter latLngConverter = new LatLngConverter();
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "buildGroundLayoutRouteString"
	)
	@ParameterizedTest
	void buildGroundLayoutRouteString(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "expectedDefaultGroundLayoutRouteString" ) String expectedDefaultGroundLayoutRouteString,
		@Property( "expectedSpecialGroundLayoutRouteString" ) String expectedSpecialGroundLayoutRouteString ) {
		
		Assertions.assertEquals(
			expectedDefaultGroundLayoutRouteString,
			route.buildGroundLayoutRouteString( true ),
			"Default"
		);
		Assertions.assertEquals(
			expectedSpecialGroundLayoutRouteString,
			route.buildGroundLayoutRouteString( false ),
			"Special"
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "splitPointsAtCrossings"
	)
	@ParameterizedTest
	void splitPointsAtCrossings(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "expectedSplittedPoints" ) List<List<PointData>> expectedSplittedPoints ) {
		
		Assertions.assertIterableEquals(
			expectedSplittedPoints,
			route.splitPointsAtCrossings()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "buildGroundLayoutRouteParts"
	)
	@ParameterizedTest
	void buildGroundLayoutRouteParts(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "expectedRouteParts" ) List<RoutePart> expectedRouteParts ) {
		
		Assertions.assertIterableEquals(
			expectedRouteParts,
			route.buildGroundLayoutRouteParts()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "buildGroundLayoutStrings"
	)
	@ParameterizedTest
	void buildGroundLayoutStrings(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "expectedGroundLayoutRouteStrings" ) List<String> expectedGroundLayoutRouteStrings ) {
		
		Assertions.assertIterableEquals(
			expectedGroundLayoutRouteStrings,
			route.buildGroundLayoutStrings( latLngConverter )
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "buildKmlRoutePointString"
	)
	@ParameterizedTest
	void buildKmlRoutePointString(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "kmlRoutePointString" ) String kmlRoutePointString ) {
		
		Assertions.assertEquals(
			kmlRoutePointString,
			route.buildKmlRoutePointString()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/route-data-test-data.json",
		data = "buildKmlProcessedRawString"
	)
	@ParameterizedTest
	void buildKmlProcessedRawString(
		@Property( "displayName" ) String displayName,
		@Property( "route" ) RouteData route,
		@Property( "kmlProcessedRawString" ) String kmlProcessedRawString ) {
		
		Assertions.assertEquals(
			kmlProcessedRawString,
			route.buildKmlProcessedRawString()
		);
	}
}