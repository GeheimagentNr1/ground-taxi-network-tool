package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import net.opengis.kml._2.Placemark;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;

import java.util.List;


class KmlLoaderTest {
	
	
	private final KmlLoader kmlLoader = new KmlLoader();
	
	@JsonClasspathSource( "de/secretsoft/ground_taxi_network_tool/services/kml-loader-test-data.json" )
	@ParameterizedTest
	void placemarkToRouteData(
		@Property( "displayName" ) String displayName,
		@Property( "placemark" ) Placemark placemark,
		@Property( "route" ) RouteData route,
		@Property( "errors" ) List<String> errors ) {
		
		Pair<RouteData, List<String>> result = kmlLoader.placemarkToRouteData( placemark );
		Assertions.assertIterableEquals(
			errors,
			result.getValue()
		);
		Assertions.assertEquals(
			route,
			result.getKey()
		);
	}
}