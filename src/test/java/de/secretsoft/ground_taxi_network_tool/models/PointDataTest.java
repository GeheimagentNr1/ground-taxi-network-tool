package de.secretsoft.ground_taxi_network_tool.models;

import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;


class PointDataTest {
	
	
	private final LatLngConverter latLngConverter = new LatLngConverter();
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/point-test-data.json",
		data = "hasNonEmptyName"
	)
	@ParameterizedTest
	void hasNonEmptyName(
		@Property( "displayName" ) String displayName,
		@Property( "pointData" ) PointData pointData,
		@Property( "result" ) boolean result ) {
		
		Assertions.assertEquals(
			result,
			pointData.hasNonEmptyName()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/point-test-data.json",
		data = "hasCrossingName"
	)
	@ParameterizedTest
	void hasCrossingName(
		@Property( "displayName" ) String displayName,
		@Property( "pointData" ) PointData pointData,
		@Property( "result" ) boolean result ) {
		
		Assertions.assertEquals(
			result,
			pointData.hasCrossingName()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/point-test-data.json",
		data = "buildKmlCoordinatesString"
	)
	@ParameterizedTest
	void buildKmlCoordinatesString(
		@Property( "displayName" ) String displayName,
		@Property( "pointData" ) PointData pointData,
		@Property( "coordinatesString" ) String coordinatesString ) {
		
		Assertions.assertEquals(
			coordinatesString,
			pointData.buildKmlCoordinatesString()
		);
	}
	
	@JsonClasspathSource(
		value = "de/secretsoft/ground_taxi_network_tool/models/point-test-data.json",
		data = "buildGroundLayoutCoordinatesString"
	)
	@ParameterizedTest
	void buildGroundLayoutCoordinatesString(
		@Property( "displayName" ) String displayName,
		@Property( "pointData" ) PointData pointData,
		@Property( "coordinatesString" ) String coordinatesString ) {
		
		Assertions.assertEquals(
			coordinatesString,
			pointData.buildGroundLayoutCoordinatesString( latLngConverter )
		);
	}
}