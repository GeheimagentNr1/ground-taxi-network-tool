package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class CoordinatesSorterTest {
	
	
	private final CoordinatesSorter coordinatesSorter = new CoordinatesSorter();
	
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/services/coordinates-sorter-test-data.json",
		data = "sort"
	)
	@ParameterizedTest
	void sort(
		@Property( "displayName" ) String displayName,
		@Property( "inputRoutes" ) List<RouteData> inputRoutes,
		@Property( "expectedRoutes" ) List<RouteData> expectedRoutes ) {
		
		coordinatesSorter.sort( inputRoutes );
		Assertions.assertIterableEquals(
			expectedRoutes,
			inputRoutes
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/services/coordinates-sorter-test-data.json",
		data = "sortPointsWithIllegalStateException"
	)
	@ParameterizedTest
	void sortPointsWithIllegalStateException(
		@Property( "displayName" ) String displayName,
		@Property( "inputPoints" ) List<PointData> inputPoints,
		@Property( "errorMessage" ) String errorMessage ) {
		
		IllegalStateException exception = Assertions.assertThrowsExactly(
			IllegalStateException.class,
			() -> coordinatesSorter.sortPoints( inputPoints )
		);
		Assertions.assertEquals( errorMessage, exception.getMessage() );
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/services/coordinates-sorter-test-data.json",
		data = "sortPoints"
	)
	@ParameterizedTest
	void sortPoints(
		@Property( "displayName" ) String displayName,
		@Property( "inputPoints" ) List<PointData> inputPoints,
		@Property( "expectedPoints" ) List<PointData> expectedPoints ) {
		
		Assertions.assertIterableEquals(
			expectedPoints,
			coordinatesSorter.sortPoints( inputPoints )
		);
	}
}