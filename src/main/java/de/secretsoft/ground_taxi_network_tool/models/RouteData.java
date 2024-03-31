package de.secretsoft.ground_taxi_network_tool.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@JsonIgnoreProperties( ignoreUnknown = true )
@JsonTypeInfo( property = "type", use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY )
@JsonSubTypes( {
	@JsonSubTypes.Type( value = Exit.class, name = "exit" ),
	@JsonSubTypes.Type( value = HoldingPoint.class, name = "holding_point" ),
	@JsonSubTypes.Type( value = LineUp.class, name = "line_up" ),
	@JsonSubTypes.Type( value = Stand.class, name = "stand" ),
	@JsonSubTypes.Type( value = Taxiout.class, name = "taxiout" ),
	@JsonSubTypes.Type( value = Taxiway.class, name = "taxiway" )
} )
public interface RouteData {
	
	
	String getRaw();
	
	String getName();
	
	List<PointData> getPoints();
	
	void setPoints( List<PointData> points );
	
	String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString );
	
	default List<List<PointData>> splitPointsAtCrossings() {
		
		List<PointData> points = getPoints();
		List<List<PointData>> splittedPoints = new ArrayList<>();
		
		List<PointData> currentSplit = new ArrayList<>();
		for( PointData point : points ) {
			currentSplit.add( point );
			if( currentSplit.size() >= 2 && point.hasCrossingName() ) {
				splittedPoints.add( currentSplit );
				currentSplit = new ArrayList<>();
				currentSplit.add( point );
			}
		}
		if( currentSplit.size() >= 2 ) {
			splittedPoints.add( currentSplit );
		}
		return splittedPoints;
	}
	
	List<RoutePart> buildGroundLayoutRouteParts();
	
	default List<String> buildGroundLayoutStrings( final LatLngConverter latLngConverter ) {
		
		return buildGroundLayoutRouteParts().stream()
			.flatMap( routePart -> {
				List<String> groundLayoutRoutePartData = new ArrayList<>();
				
				groundLayoutRoutePartData.add( buildGroundLayoutRouteString( routePart.isUseDefaultGroundLayoutRouteString() ) );
				groundLayoutRoutePartData.addAll(
					routePart.getPoints().stream()
						.map( point -> point.buildGroundLayoutCoordinatesString( latLngConverter ) )
						.toList()
				);
				return groundLayoutRoutePartData.stream();
			} )
			.toList();
	}
	
	default String buildKmlRoutePointString() {
		
		List<PointData> points = getPoints();
		List<Integer> crossingIndexes = IntStream.range( 0, points.size() )
			.filter( value -> points.get( value ).hasNonEmptyName() )
			.boxed()
			.toList();
		List<String> routePoints = new ArrayList<>();
		if( crossingIndexes.size() == 2 &&
			crossingIndexes.contains( 0 ) &&
			crossingIndexes.contains( points.size() - 1 ) ) {
			routePoints.add( points.getFirst().getName() );
			routePoints.add( points.getLast().getName() );
		} else {
			points.forEach( pointData -> routePoints.add( pointData.getName() ) );
		}
		return String.join( "-", routePoints );
	}
	
	String buildKmlProcessedRawString();
}
