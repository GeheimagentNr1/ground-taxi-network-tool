package de.secretsoft.ground_taxi_network_tool.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointCrossingData {
	
	
	private boolean foundAnotherPoint;
	
	private PointData pointData;
}
