package de.secretsoft.ground_taxi_network_tool.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stand implements RouteData {
	
	
	private String raw;
	
	private String name;
	
	private boolean gate;
	
	private boolean taxiout;
	
	private int speed;
	
	private List<PointData> points;
	
	@Override
	public String buildGroundLayoutRouteString( boolean useDefaultGroundLayoutRouteString ) {
		
		if(useDefaultGroundLayoutRouteString) {
			return String.format(
				"TAXI:%s%s:%d:1",
				gate ? "G" : "S",
				name,
				speed
			);
		} else {
			return String.format(
				"TAXI:%s%s:%d:1:%s%s",
				gate ? "G" : "S",
				name,
				speed,
				gate ? "G" : "S",
				name
			);
		}
	}
	
	@Override
	public List<RoutePart> buildGroundLayoutRouteParts() {
		
		return splitPointsAtCrossings().stream()
			.map( splittedPoints -> RoutePart.builder()
				.useDefaultGroundLayoutRouteString(
					splittedPoints.stream()
						.noneMatch( pointData -> name.matches( pointData.getName() ) )
				)
				.points( splittedPoints )
				.build() )
			.toList();
	}
	
	@Override
	public String buildKmlProcessedRawString() {
		
		return String.format(
			"%s; %s; %s; %s; %s;",
			gate ? "Gate" : "Stand",
			name,
			buildKmlRoutePointString(),
			taxiout ? "1" : "0",
			speed
		);
	}
}
