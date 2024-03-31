package de.secretsoft.ground_taxi_network_tool.job.steps.write_ground_layout;

import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.support.AbstractFileItemWriter;


@RequiredArgsConstructor
public class GroundLayoutFileItemWriter extends AbstractFileItemWriter<RouteData> {
	
	
	private final LatLngConverter latLngConverter;
	
	@Override
	protected String doWrite( Chunk<? extends RouteData> items ) {
		
		StringBuilder lines = new StringBuilder();
		
		for( RouteData route : items ) {
			for( String line : route.buildGroundLayoutStrings( latLngConverter ) ) {
				lines.append( line ).append( this.lineSeparator );
			}
		}
		return lines.toString();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
	
	}
}
