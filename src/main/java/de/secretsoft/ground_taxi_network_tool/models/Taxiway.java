package de.secretsoft.ground_taxi_network_tool.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taxiway implements RouteData {
	
	
	private String raw;
	
	private String name;
	
	private boolean hasStandsTaxiouts;
	
	private boolean standOrTaxioutAdded;
	
	private int speed;
	
	private List<PointData> points;
	
	@Override
	public String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString ) {
		
		return String.format(
			"TAXI:%s:%d:1",
			name,
			speed
		);
	}
	
	@Override
	public List<RoutePart> buildGroundLayoutRouteParts() {
		
		return splitPointsAtCrossings().stream()
			.map( splittedPoints -> RoutePart.builder()
				.useDefaultGroundLayoutRouteString( true )
				.points( splittedPoints )
				.build() )
			.toList();
	}
	
	@Override
	public String buildKmlProcessedRawString() {
		
		return String.format(
			"%s; %s; %s; %s;",
			name,
			buildKmlRoutePointString(),
			hasStandsTaxiouts ? "1" : "0",
			speed
		);
	}
}
