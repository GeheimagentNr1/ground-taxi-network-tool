package de.secretsoft.ground_taxi_network_tool.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;


class LatLngConverterTest {
	
	
	private final LatLngConverter latLngConverter = new LatLngConverter();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/lat-lng-converter-test-data.json" )
	@ParameterizedTest
	void processCoordinates(
		@Property( "displayName" ) String displayName,
		@Property( "inputLatitude" ) double inputLatitude,
		@Property( "inputLongitude" ) double inputLongitude,
		@Property( "expectedCoordinatesString" ) String expectedCoordinatesString ) {
		
		Assertions.assertEquals(
			expectedCoordinatesString,
			latLngConverter.processCoordinates(
				inputLatitude,
				inputLongitude,
				( latitudeOrientation, latitude, longitudeOrientation, longitude ) ->
					latitude + " " + latitudeOrientation + ", " + longitude + " " + longitudeOrientation,
				( degrees, minutes, seconds ) -> degrees + "°" + minutes + "'" + seconds + "\""
			)
		);
	}
}