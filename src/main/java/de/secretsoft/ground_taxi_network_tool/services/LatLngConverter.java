package de.secretsoft.ground_taxi_network_tool.services;

import org.springframework.stereotype.Service;


@Service
public class LatLngConverter {
	
	
	private static final String[] ORIENTATIONS = "N/S/E/W".split( "/" );
	
	private static final int SECONDS_DECIMAL_POINTS = 3;

	/*public static void main(String[] args) {
		final double[] coordinates = { 10.00041074913203, 53.62890514380151 };
		String dmsResult = processCoordinates(coordinates);
		final String coords_txt = coordinates[1] + "," + coordinates[0];
		System.out.println(coords_txt + " converted -> " + dmsResult);
	}*/
	
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

	/*private static String processCoordinates(double[] coordinates) {
		String converted0 = decimalToDMS(coordinates[1], singleCoordinateFormater );
		final String dmsLat = coordinates[0] > 0 ? ORIENTATIONS[0] : ORIENTATIONS[1];
		converted0 = converted0.concat(" ").concat(dmsLat);

		String converted1 = decimalToDMS(coordinates[0], singleCoordinateFormater );
		final String dmsLng = coordinates[1] > 0 ? ORIENTATIONS[2] : ORIENTATIONS[3];
		converted1 = converted1.concat(" ").concat(dmsLng);

		return converted0.concat(", ").concat(converted1);
	}*/
	
	private static String decimalToDMS( double coord, SingleCoordinateFormater singleCoordinateFormater ) {
		
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
	
	private static double round( double value ) {
		
		double d = StrictMath.pow( 10, SECONDS_DECIMAL_POINTS );
		return Math.round( value * d ) / d;
	}
}