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
public class LineUp implements RouteData {
	
	
	private String raw;
	
	private String name;
	
	private String runwayLineUp;
	
	private String runwayExit;
	
	private int speed;
	
	private boolean found;
	
	private List<PointData> points;
	
	@Override
	public String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString ) {
		
		return String.format(
			"TAXI:%s:%d:1:HP %s %s LU",
			name,
			speed,
			runwayLineUp,
			name
		);
	}
	
	@Override
	public List<RoutePart> buildGroundLayoutRouteParts() {
		
		return List.of(
			RoutePart.builder()
				.useDefaultGroundLayoutRouteString( true )
				.points( points )
				.build()
		);
	}
	
	@Override
	public String buildKmlProcessedRawString() {
		
		return String.format(
			"LU; %s; %s; %s; %s; %s;",
			runwayLineUp,
			runwayExit,
			name,
			buildKmlRoutePointString(),
			speed
		);
	}
}
