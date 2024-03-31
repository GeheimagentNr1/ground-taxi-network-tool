package de.secretsoft.ground_taxi_network_tool.job.steps.read_kml;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.services.KmlLoader;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import net.opengis.kml._2.Placemark;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;


@RequiredArgsConstructor
public class ReadKmlProcessor implements ItemProcessor<Placemark, RouteData> {
	
	
	private final RoutesHolder routesHolder;
	
	private final KmlLoader kmlLoader;
	
	@Override
	public RouteData process( Placemark item ) {
		
		Pair<RouteData, List<String>> result = kmlLoader.placemarkToRouteData( item );
		routesHolder.addErrors( result.getValue() );
		return result.getKey();
	}
}
