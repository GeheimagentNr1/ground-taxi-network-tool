package de.secretsoft.ground_taxi_network_tool.models;


import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


@Data
@Builder( toBuilder = true )
@NoArgsConstructor
@AllArgsConstructor
public class PointData {
	
	
	public static final String EMPTY_NAME = "_";
	
	public static final String END_NAME = "END";
	
	private String name;
	
	private double latitude;
	
	private double longitude;
	
	private boolean start;
	
	public boolean hasNonEmptyName() {
		
		return !Objects.equals( EMPTY_NAME, name );
	}
	
	public boolean hasCrossingName() {
		
		return hasNonEmptyName() && !Objects.equals( END_NAME, name );
	}
	
	public String buildKmlCoordinatesString() {
		
		return String.format( "%s,%s,0", longitude, latitude );
	}
	
	public String buildGroundLayoutCoordinatesString( final LatLngConverter latLngConverter ) {
		
		return latLngConverter.processCoordinates(
			latitude,
			longitude,
			( latitudeOrientation, latitudeString, longitudeOrientation, longitudeString ) ->
				"COORD:" + latitudeOrientation + latitudeString + ":" + longitudeOrientation + longitudeString +
					( EMPTY_NAME.equals( name ) ? "" : ";" + name ),
			( degrees, minutes, seconds ) ->
				StringUtils.leftPad( String.valueOf( degrees ), 3, "0" ) + "." + minutes + "." + seconds
		);
	}
}
