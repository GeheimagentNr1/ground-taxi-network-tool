package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.*;
import net.opengis.kml._2.Placemark;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class KmlLoader {
	
	
	private static final Pattern LINE_UP_PATTERN = Pattern.compile(
		"[ ]*LU[ ]*;[ ]*[0-3][0-9][ ]*;[ ]*[0-3][0-9][ ]*;[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+(-[^; \\n]+)+" +
			"([ ]*;[ ]*\\d+)?[ ]*;?[ ]*"
	);
	
	private static final Pattern EXIT_PATTERN = Pattern.compile(
		"[ ]*Exit[ ]*;[ ]*[0-3][0-9]([ ]*,[ ]*[0-3][0-9])*[ ]*;[ ]*[0-3][0-9]-(LEFT|RIGHT)([ ]*,[ ]*[0-3][0-9]-" +
			"(LEFT|RIGHT))*[ ]*;[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+(-[^; \\n]+)+([ ]*;[ ]*\\d+)?[ ]*;?[ ]*"
	);
	
	private static final Pattern STAND_PATTERN = Pattern.compile(
		"[ ]*(Stand|Gate)[ ]*;[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+(-[^; \\n]+)+([ ]*;[ ]*[0-1][ ]*(;[ ]*\\d+)?)?[ ]*;?[ ]*"
	);
	
	private static final Pattern TAXIOUT_PATTERN = Pattern.compile(
		"[ ]*Taxiout[ ]*;[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+(-[^; \\n]+)+([ ]*;[ ]*\\d+)?[ ]*;?[ ]*"
	);
	
	private static final Pattern TAXIWAY_PATTERN = Pattern.compile(
		"[ ]*[^; \\n]+[ ]*;[ ]*[^; \\n]+(-[^; \\n]+)+([ ]*;[ ]*[0-1][ ]*(;[ ]*\\d+)?)?[ ]*;?[ ]*"
	);
	
	private static final int DEFAULT_SPEED = 20;
	
	public Pair<RouteData, List<String>> placemarkToRouteData( Placemark placemark ) {
		
		String raw = placemark.getName();
		String[] parts = Arrays.stream( raw.split( ";" ) )
			.map( String::trim )
			.toArray( String[]::new );
		List<String> coordinates = placemark.getLineString().getCoordinates();
		
		if( LINE_UP_PATTERN.matcher( raw ).matches() ) {
			List<PointData> points = buildPoints( parts[4], coordinates );
			if( points == null ) {
				return buildPointsErrorResult( raw );
			} else {
				return buildSuccessResult(
					LineUp.builder()
						.raw( raw )
						.name( parts[3] )
						.runwayLineUp( parts[1] )
						.runwayExit( parts[2] )
						.speed( parts.length >= 6 ? Integer.parseInt( parts[5] ) : DEFAULT_SPEED )
						.points( points )
						.build()
				);
			}
		}
		if( EXIT_PATTERN.matcher( raw ).matches() ) {
			List<PointData> points = buildPoints( parts[4], coordinates );
			if( points == null ) {
				return buildPointsErrorResult( raw );
			} else {
				return buildSuccessResult(
					Exit.builder()
						.raw( raw )
						.name( parts[3] )
						.lineUpRunways(
							Arrays.stream( parts[1].split( "," ) )
								.map( String::trim )
								.collect( Collectors.toList() )
						)
						.exitRunways(
							Arrays.stream( parts[2].split( "," ) )
								.map( String::trim )
								.map( exitRunway -> {
									String[] exitRunwayParts = exitRunway.split( "-" );
									return ExitRunway.builder()
										.runway( exitRunwayParts[0] )
										.diretion( ExitDirection.valueOf( exitRunwayParts[1] ) )
										.build();
								} )
								.collect( Collectors.toList() ) )
						.speed( parts.length >= 6 ? Integer.parseInt( parts[5] ) : DEFAULT_SPEED )
						.points( points )
						.build()
				);
			}
		}
		if( STAND_PATTERN.matcher( raw ).matches() ) {
			List<PointData> points = buildPoints( parts[2], coordinates );
			if( points == null ) {
				return buildPointsErrorResult( raw );
			} else {
				return buildSuccessResult(
					Stand.builder()
						.raw( raw )
						.name( parts[1] )
						.gate( "Gate".equals( parts[0] ) )
						.taxiout( parts.length >= 4 && "1".equals( parts[3] ) )
						.speed( parts.length >= 5 ? Integer.parseInt( parts[4] ) : DEFAULT_SPEED )
						.points( points )
						.build()
				);
			}
		}
		if( TAXIOUT_PATTERN.matcher( raw ).matches() ) {
			List<PointData> points = buildPoints( parts[3], coordinates );
			if( points == null ) {
				return buildPointsErrorResult( raw );
			} else {
				return buildSuccessResult(
					Taxiout.builder()
						.raw( raw )
						.name( parts[1] )
						.stand( parts[2] )
						.speed( parts.length >= 5 ? Integer.parseInt( parts[4] ) : DEFAULT_SPEED )
						.points( points )
						.build()
				);
			}
		}
		if( TAXIWAY_PATTERN.matcher( raw ).matches() ) {
			List<PointData> points = buildPoints( parts[1], coordinates );
			if( points == null ) {
				return buildPointsErrorResult( raw );
			} else {
				return buildSuccessResult(
					Taxiway.builder()
						.raw( raw )
						.name( parts[0] )
						.hasStandsTaxiouts( parts.length >= 3 && "1".equals( parts[2] ) )
						.speed( parts.length >= 4 ? Integer.parseInt( parts[3] ) : DEFAULT_SPEED )
						.points( points )
						.build()
				);
			}
		}
		return Pair.of(
			null,
			List.of(
				String.format(
					"\"%s\" has an unknown pattern/type",
					raw
				)
			)
		);
	}
	
	private List<PointData> buildPoints( String routeString, List<String> coordinates ) {
		
		String[] routePoints = routeString.split( "-" );
		if( routePoints.length != 2 && routePoints.length != coordinates.size() ) {
			return null;
		}
		List<PointData> points = coordinates.stream()
			.map( coordinate -> {
				String[] coordinateParts = coordinate.split( "," );
				return PointData.builder()
					.name( "_" )
					.latitude( Double.parseDouble( coordinateParts[1] ) )
					.longitude( Double.parseDouble( coordinateParts[0] ) )
					.build();
			} )
			.collect( Collectors.toList() );
		points.getFirst().setStart( true );
		if( routePoints.length == 2 ) {
			points.getFirst().setName( routePoints[0] );
			points.getLast().setName( routePoints[1] );
		} else {
			for( int i = 0; i < routePoints.length; i++ ) {
				points.get( i ).setName( routePoints[i] );
			}
		}
		return points;
	}
	
	private Pair<RouteData, List<String>> buildPointsErrorResult( String raw ) {
		
		return Pair.of(
			null,
			List.of(
				String.format(
					"\"%s\" has a number of route point, that not matches the coordinates count",
					raw
				)
			)
		);
	}
	
	private Pair<RouteData, List<String>> buildSuccessResult( RouteData route ) {
		
		return Pair.of(
			route,
			List.of()
		);
	}
}
