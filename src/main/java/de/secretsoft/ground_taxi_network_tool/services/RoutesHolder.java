package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Getter
@Service
public class RoutesHolder {
	
	
	private final List<RouteData> routes = new ArrayList<>();
	
	private final List<String> errors = new ArrayList<>();
	
	public void addRoutes( List<RouteData> pRoutes ) {
		
		routes.addAll( pRoutes );
	}
	
	public void addErrors( List<String> pErrors ) {
		
		errors.addAll( pErrors );
	}
}
