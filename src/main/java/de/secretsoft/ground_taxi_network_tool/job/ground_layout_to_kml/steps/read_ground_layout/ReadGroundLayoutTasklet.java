package de.secretsoft.ground_taxi_network_tool.job.ground_layout_to_kml.steps.read_ground_layout;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.models.Taxiway;
import de.secretsoft.ground_taxi_network_tool.services.LatLngConverter;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class ReadGroundLayoutTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext )
		throws IOException {
		
		LatLngConverter latLngConverter = new LatLngConverter();
		try( BufferedReader bufferedReader =
			new BufferedReader( new FileReader( "S:\\Develop_Projects\\ground-taxi-network-tool\\data\\EDDV\\Live\\test.txt" ) ) ) {
			RouteData currentRoute = null;
			for( int lineIndex = 0; ; lineIndex++ ){
				String line = bufferedReader.readLine();
				if( line == null ) {
					break;
				}
				if( !line.isEmpty() && line.charAt( 0 ) != ';' && !line.startsWith( "[GROUND]" )  ) {
					int index = line.indexOf( ';' );
					if( index != -1 ) {
						line = line.substring( 0, index - 1 );
					}
					if( line.startsWith( "COORD" )) {
						List<Integer> pointIndexes = new ArrayList<>();
						List<Integer> doublePointIndexes = new ArrayList<>();
						for(int i = 0; i < line.length(); i++){
							if(line.charAt(i) == '.'){
								pointIndexes.add(i);
							}
							if(line.charAt(i) == ':'){
								doublePointIndexes.add(i);
							}
						}
						if(pointIndexes.size() != 6) {
							throw new IllegalStateException("Test " + pointIndexes.size());
						}
						currentRoute.getPoints().add(
							PointData.builder()
								.name( "_" )
								.latitude( latLngConverter.dmsToDecimal(
									Double.parseDouble( line.substring( 7, pointIndexes.get( 0 ) ) ),
									Double.parseDouble( line.substring( pointIndexes.get( 0 ) + 1, pointIndexes.get( 1 ) ) ),
									Double.parseDouble( line.substring( pointIndexes.get( 1 ) + 1, doublePointIndexes.get( 1 ) ) )
								) )
								.longitude( latLngConverter.dmsToDecimal(
									Double.parseDouble( line.substring( pointIndexes.get( 3 ) - 3, pointIndexes.get( 3 ) ) ),
									Double.parseDouble( line.substring( pointIndexes.get( 3 ) + 1, pointIndexes.get( 4 ) ) ),
									Double.parseDouble( line.substring( pointIndexes.get( 4 ) + 1 ) )
								) )
								.start( false )
								.build()
						);
					} else {
						currentRoute = Taxiway.builder()
							.raw( line )
							.name( "test" )
							.hasStandsTaxiouts( false )
							.speed( 0 )
							.points( new ArrayList<>() )
							.build();
						routesHolder.addRoutes( List.of(currentRoute) );
					}
				}
			}
		}
		return RepeatStatus.FINISHED;
	}
}
