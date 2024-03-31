package de.secretsoft.ground_taxi_network_tool.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder( toBuilder = true )
@NoArgsConstructor
@AllArgsConstructor
public class Exit implements RouteData {
	
	
	private String raw;
	
	private String name;
	
	private List<String> lineUpRunways;
	
	private List<ExitRunway> exitRunways;
	
	private int speed;
	
	private boolean foundLineUp;
	
	private List<PointData> points;
	
	@Override
	public String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString ) {
		
		ExitRunway exitRunway = exitRunways.getFirst();
		return String.format(
			"EXIT:%s:%s:%s:%d",
			exitRunway.getRunway(),
			name,
			exitRunway.getDiretion().name(),
			speed
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
			"Exit; %s; %s; %s; %s; %s;",
			String.join( ", ", lineUpRunways ),
			exitRunways.stream()
				.map( exitRunway -> exitRunway.getRunway() + "-" + exitRunway.getDiretion().name() )
				.collect( Collectors.joining( ", " ) ),
			name,
			buildKmlRoutePointString(),
			speed
		);
	}
}
