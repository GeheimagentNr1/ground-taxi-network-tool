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
public class RoutePart {
	
	private boolean useDefaultGroundLayoutRouteString;
	
	private List<PointData> points;
}
