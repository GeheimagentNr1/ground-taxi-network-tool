package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.models.Stand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class CoordinatesSorter {
	
	
	public void sort( List<RouteData> routes ) {
		
		for( RouteData route : routes ) {
			if( route instanceof Stand stand &&
				Objects.equals( route.getPoints().getFirst().getName(), stand.getName() ) ) {
				route.getPoints().getFirst().setStart( false );
				route.getPoints().getLast().setStart( true );
			}
			route.setPoints( sortPoints( route.getPoints() ) );
		}
	}
	
	List<PointData> sortPoints( List<PointData> points ) {
		
		List<PointData> startPoints = points.stream()
			.filter( PointData::isStart )
			.toList();
		if( startPoints.isEmpty() ) {
			throw new IllegalStateException( "No start point found" );
		}
		if( startPoints.size() > 1 ) {
			throw new IllegalStateException( "To many start points found" );
		}
		List<PointData> sortedPoints = new ArrayList<>();
		List<PointData> toSortPoints = new ArrayList<>( points );
		PointData start = startPoints.getFirst();
		sortedPoints.add( start );
		toSortPoints.remove( start );
		
		PointData currentPoint = start;
		while( !toSortPoints.isEmpty() ) {
			PointData foundPoint = null;
			double foundDistance = Double.MAX_VALUE;
			
			for( PointData toSortPoint : toSortPoints ) {
				double distance = calculatedPointDistance( Objects.requireNonNull( currentPoint ), toSortPoint );
				if( distance < foundDistance ) {
					foundPoint = toSortPoint;
					foundDistance = distance;
				}
			}
			
			sortedPoints.add( foundPoint );
			toSortPoints.remove( foundPoint );
			currentPoint = foundPoint;
		}
		
		return sortedPoints;
	}
	
	private double calculatedPointDistance( PointData first, PointData second ) {
		
		return Math.sqrt(
			StrictMath.pow( second.getLongitude() - first.getLongitude(), 2 ) +
				StrictMath.pow( second.getLatitude() - first.getLatitude(), 2 )
		);
	}
}
