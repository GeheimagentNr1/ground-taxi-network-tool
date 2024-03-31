package de.secretsoft.ground_taxi_network_tool.job.steps.read_kml;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.function.Function;


@RequiredArgsConstructor
public class ReadKmlWriter implements ItemWriter<RouteData> {
	
	
	private final RoutesHolder routesHolder;
	
	@Override
	public void write( Chunk<? extends RouteData> chunk ) {
		
		routesHolder.addRoutes(
			chunk.getItems().stream()
				.map( (Function<RouteData, RouteData>)routeData -> routeData )
				.toList()
		);
	}
}
