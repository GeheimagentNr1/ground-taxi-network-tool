package de.secretsoft.ground_taxi_network_tool.job.steps.add_gate_and_taxiout_points;

import de.secretsoft.ground_taxi_network_tool.services.GateAndTaxioutPointAdder;
import de.secretsoft.ground_taxi_network_tool.services.RoutesHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


@RequiredArgsConstructor
public class AddGateAndTaxioutPointsTasklet implements Tasklet {
	
	
	private final RoutesHolder routesHolder;
	
	private final GateAndTaxioutPointAdder gateAndTaxioutPointAdder;
	
	@Override
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) {
		
		routesHolder.addErrors(
			gateAndTaxioutPointAdder.addGatePoints( routesHolder.getRoutes() )
		);
		return RepeatStatus.FINISHED;
	}
}
