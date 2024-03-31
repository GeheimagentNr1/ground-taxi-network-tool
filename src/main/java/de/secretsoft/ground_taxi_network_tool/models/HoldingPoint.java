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
public class HoldingPoint implements RouteData {
	
	
	private String raw;
	
	private String name;
	
	private List<String> lineUpRunways;
	
	private int speed;
	
	private List<PointData> points;
	
	@Override
	public String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString ) {
		
		return String.format(
			"TAXI:%s:%d:1:HP %s %s",
			name,
			speed,
			String.join( "/", lineUpRunways ),
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
			"HP; %s; %s; %s; %s;",
			String.join( ", ", lineUpRunways ),
			name,
			buildKmlRoutePointString(),
			speed
		);
	}
}
