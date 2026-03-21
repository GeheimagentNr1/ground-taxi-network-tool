package de.secretsoft.ground_taxi_network_tool.services;

import org.springframework.stereotype.Service;


@Service
public class LatLngConverter {
	
	
	private static final String[] ORIENTATIONS = "N/S/E/W".split( "/" );
	
	private static final int SECONDS_DECIMAL_POINTS = 3;
	
	public String processCoordinates(
		double latitude,
		double longitude,
		CoordinateFormater coordinateFormater,
		SingleCoordinateFormater singleCoordinateFormater ) {
		
		return coordinateFormater.format(
			latitude > 0 ? ORIENTATIONS[0] : ORIENTATIONS[1],
			decimalToDMS( latitude, singleCoordinateFormater ),
			longitude > 0 ? ORIENTATIONS[2] : ORIENTATIONS[3],
			decimalToDMS( longitude, singleCoordinateFormater )
		);
	}
	
	private String decimalToDMS( double coord, SingleCoordinateFormater singleCoordinateFormater ) {
		
		double minutesBase = coord % 1;
		int degrees = (int)coord;
		if( degrees < 0 ) {
			degrees *= -1;
		}
		
		coord = minutesBase * 60.0;
		double secondsBase = coord % 1;
		int minutes = (int)coord;
		if( minutes < 0 ) {
			minutes *= -1;
		}
		
		coord = secondsBase * 60.0;
		double seconds = round( coord );
		if( seconds < 0 ) {
			seconds *= -1;
		}
		return singleCoordinateFormater.format( degrees, minutes, seconds );
	}
	
	public double dmsToDecimal( double degrees, double minutes, double seconds ) {
		
		return degrees + minutes / 60.0 + seconds / 60.0 / 60.0;
	}
	
	private double round( double value ) {
		
		double d = StrictMath.pow( 10, SECONDS_DECIMAL_POINTS );
		return Math.round( value * d ) / d;
	}
}