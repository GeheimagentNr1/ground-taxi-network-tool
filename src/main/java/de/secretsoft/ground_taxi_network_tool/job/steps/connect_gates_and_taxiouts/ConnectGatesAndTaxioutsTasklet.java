package de.secretsoft.ground_taxi_network_tool.job.steps.connect_gates_and_taxiouts;

import de.secretsoft.ground_taxi_network_tool.services.GateTaxioutConnector;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class ConnectGatesAndTaxioutsTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final GateTaxioutConnector gateTaxioutConnector;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		routesHolder.addErrors(
			gateTaxioutConnector.connectGatesAndTaxiouts( routesHolder.getRoutes() )
		);
		return RepeatStatus.FINISHED;
	}
}