package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.*;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CrossingConnector {
	
	
	public List<String> connectCrossings( List<RouteData> routes ) {
		
		Map<String, PointCrossingData> crossingPoints = new HashMap<>();
		for( RouteData route : routes ) {
			if( !( route instanceof Stand ) && !( route instanceof Taxiout ) ) {
				for( PointData point : route.getPoints() ) {
					if( point.hasCrossingName() &&
						!Objects.equals( route.getName(), point.getName() ) ) {
						String name = point.getName();
						PointCrossingData pointCrossingData = crossingPoints.get( name );
						
						if( pointCrossingData == null ) {
							crossingPoints.put(
								name,
								PointCrossingData.builder()
									.foundAnotherPoint( false )
									.pointData( point )
									.build()
							);
						} else {
							pointCrossingData.setFoundAnotherPoint( true );
							point.setLatitude( pointCrossingData.getPointData().getLatitude() );
							point.setLongitude( pointCrossingData.getPointData().getLongitude() );
						}
					}
				}
			}
		}
		List<String> errors = new ArrayList<>();
		crossingPoints.values().stream()
			.filter( pointCrossingData -> !pointCrossingData.isFoundAnotherPoint() )
			.forEach( pointCrossingData -> errors.add( String.format(
				"Crossing \"%s\" has no matching point",
				pointCrossingData.getPointData().getName()
			) ) );
		return errors;
	}
}
