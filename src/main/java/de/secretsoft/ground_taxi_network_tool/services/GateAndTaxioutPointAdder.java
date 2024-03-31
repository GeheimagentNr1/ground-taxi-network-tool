package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.models.Stand;
import de.secretsoft.ground_taxi_network_tool.models.Taxiout;
import de.secretsoft.ground_taxi_network_tool.models.Taxiway;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class GateAndTaxioutPointAdder {
	
	
	public List<String> addGatePoints( List<RouteData> routes ) {
		
		List<Taxiway> standOrTaxioutTaxiways = routes.stream()
			.filter( Taxiway.class::isInstance )
			.map( Taxiway.class::cast )
			.filter( Taxiway::isHasStandsTaxiouts )
			.toList();
		
		List<RouteData> standsAndTaxiouts = routes.stream()
			.filter( routeData -> routeData instanceof Stand || routeData instanceof Taxiout )
			.toList();
		
		List<String> errors = new ArrayList<>();
		standsAndTaxiouts.forEach( standOrTaxiout -> standOrTaxiout.getPoints().stream()
			.filter( pointData -> standOrTaxiout instanceof Stand && !Objects.equals(
				standOrTaxiout.getName(),
				pointData.getName()
			) ||
				standOrTaxiout instanceof Taxiout taxiout && !Objects.equals(
					taxiout.getStand(),
					pointData.getName()
				) )
			.forEach( pointData -> {
				boolean notFound = true;
				for( Taxiway taxiway : standOrTaxioutTaxiways ) {
					if( Objects.equals( taxiway.getName(), pointData.getName() ) ) {
						taxiway.getPoints().add(
							pointData.toBuilder()
								.start( false )
								.build()
						);
						taxiway.setStandOrTaxioutAdded( true );
						notFound = false;
					}
				}
				if( notFound ) {
					errors.add( String.format(
						"For stand, gate or taxiout \"%s\" no stand/taxiout taxiway \"%s\" found.",
						standOrTaxiout.getRaw(),
						pointData.getName()
					) );
				}
			} ) );
		standOrTaxioutTaxiways.stream()
			.filter( taxiway -> taxiway.isHasStandsTaxiouts() && !taxiway.isStandOrTaxioutAdded() )
			.forEach( taxiway -> errors.add( String.format(
				"Taxiway \"%s\" is marked as stand/taxiout taxiway but no standsAndTaxiouts where added.",
				taxiway.getRaw()
			) ) );
		return errors;
	}
}
