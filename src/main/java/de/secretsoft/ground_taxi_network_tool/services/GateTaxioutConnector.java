package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.*;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class GateTaxioutConnector {
	
	
	public List<String> connectGatesAndTaxiouts( List<RouteData> routes ) {
		
		Map<String, PointCrossingData> crossingPoints = new HashMap<>();
		
		for( RouteData route : routes ) {
			if( route instanceof Stand stand && stand.isTaxiout() ) {
				String name = route.getName();
				for( PointData point : route.getPoints() ) {
					if( Objects.equals( name, point.getName() ) ) {
						crossingPoints.put(
							name,
							PointCrossingData.builder()
								.foundAnotherPoint( false )
								.pointData( point )
								.build()
						);
					}
				}
			}
		}
		List<String> errors = new ArrayList<>();
		for( RouteData route : routes ) {
			if( route instanceof Taxiout taxiout ) {
				boolean notFound = true;
				for( PointData point : route.getPoints() ) {
					String name = point.getName();
					if( Objects.equals( taxiout.getStand(), name ) ) {
						PointCrossingData pointCrossingData = crossingPoints.get( name );
						if( pointCrossingData != null ) {
							pointCrossingData.setFoundAnotherPoint( true );
							point.setLatitude( pointCrossingData.getPointData().getLatitude() );
							point.setLongitude( pointCrossingData.getPointData().getLongitude() );
							notFound = false;
						}
					}
				}
				if( notFound ) {
					errors.add( String.format(
						"Taxiout \"%s\" has no stand found",
						route.getRaw()
					) );
				}
			}
		}
		crossingPoints.values().stream()
			.filter( pointCrossingData -> !pointCrossingData.isFoundAnotherPoint() )
			.forEach( pointCrossingData -> errors.add( String.format(
				"Stand \"%s\"is marked as taxiout stand, but has no taxiout",
				pointCrossingData.getPointData().getName()
			) ) );
		return errors;
	}
}
