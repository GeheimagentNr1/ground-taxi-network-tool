package de.secretsoft.ground_taxi_network_tool.services;

import com.sun.source.doctree.SeeTree;
import de.secretsoft.ground_taxi_network_tool.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ExitBuilder {
	
	
	public List<String> processExits( List<RouteData> routes ) {
		
		List<Exit> exits = routes.stream()
			.filter( Exit.class::isInstance )
			.map( Exit.class::cast )
			.toList();
		List<String> errors = new ArrayList<>();
		
		for( Exit exit : exits ) {
			routes.remove( exit );
			routes.addAll( processExit( routes, exit, errors ) );
		}
		routes.stream()
			.filter( LineUp.class::isInstance )
			.map( LineUp.class::cast )
			.filter( lineUp -> !lineUp.isFound() )
			.forEach( lineUp -> errors.add( String.format(
				"LineUp \"%s\" has not exit",
				lineUp.getRaw()
			) ) );
		return errors;
	}
	
	private List<RouteData> processExit( List<RouteData> routes, Exit exit, List<String> errors ) {
		
		List<String> exitlineUpRunways = exit.getLineUpRunways();
		List<String> exitExitRunways = exit.getExitRunways().stream()
			.map( ExitRunway::getRunway )
			.toList();
		List<LineUp> lineUps = routes.stream()
			.filter( LineUp.class::isInstance )
			.map( LineUp.class::cast )
			.filter( lineUp ->
				Objects.equals( exit.getName(), lineUp.getName() ) &&
					( exitlineUpRunways.contains( lineUp.getRunwayLineUp() ) ||
						exitExitRunways.contains( lineUp.getRunwayExit() ) )
			)
			.toList();
		
		Set<String> lineUpslineUpRunways = lineUps.stream()
			.map( LineUp::getRunwayLineUp )
			.collect( Collectors.toUnmodifiableSet());
		if( !lineUpslineUpRunways.containsAll( exitlineUpRunways ) ) {
			errors.add( String.format(
				"Exit \"%s\" line up runway mismatch. Exit line up runways: \"%s\" Line Up line up runways: \"%s\"",
				exit.getRaw(),
				String.join( ", ", exitlineUpRunways ),
				String.join( ", ", lineUpslineUpRunways )
			) );
		}
		List<String> lineUpsExitRunways = lineUps.stream()
			.map( LineUp::getRunwayExit )
			.distinct()
			.sorted()
			.toList();
		if( !Objects.equals(
			exitExitRunways,
			lineUpsExitRunways
		) ) {
			errors.add( String.format(
				"Exit \"%s\" exit runway mismatch. Exit exit runways: \"%s\" Line Up exit runways: \"%s\"",
				exit.getRaw(),
				String.join( ", ", exitExitRunways ),
				String.join( ", ", lineUpsExitRunways )
			) );
		}
		
		String matchingPoint = null;
		List<String> exitCrossingNames = exit.getPoints().stream()
			.filter( PointData::hasCrossingName )
			.map( PointData::getName )
			.toList();
		List<RouteData> newRoutes = new ArrayList<>();
		for( LineUp lineUp : lineUps ) {
			Exit newExit = exit.toBuilder()
				.lineUpRunways( List.of() )
				.exitRunways(
					exit.getExitRunways().stream()
						.filter( exitRunway -> Objects.equals( exitRunway.getRunway(), lineUp.getRunwayExit() ) )
						.toList()
				)
				.speed( lineUp.getSpeed() )
				.foundLineUp( true )
				.points(
					exit.getPoints().stream()
						.map( pointData -> pointData.toBuilder()
							.start( false )
							.build() )
						.collect( Collectors.toList() )
				)
				.build();
			List<String> lineUpCrossingNames = lineUp.getPoints().stream()
				.filter( PointData::hasCrossingName )
				.map( PointData::getName )
				.toList();
			if( matchingPoint == null ) {
				matchingPoint = exitCrossingNames.stream()
					.filter( lineUpCrossingNames::contains )
					.findFirst()
					.orElse( null );
			}
			if( lineUpCrossingNames.contains( matchingPoint ) ) {
				String finalMatchingPoint = matchingPoint;
				List<PointData> lineUpExitPoints = lineUp.getPoints().stream()
					.map( pointData -> pointData.toBuilder()
						.start( false )
						.build() )
					.toList();
				if( Objects.equals( lineUpExitPoints.getFirst().getName(), matchingPoint ) ) {
					lineUpExitPoints.getLast().setStart( true );
				} else {
					lineUpExitPoints.getFirst().setStart( true );
				}
				List<PointData> lineUpPoints = lineUp.getPoints();
				lineUpPoints.forEach( pointData -> pointData.setStart( false ) );
				if( Objects.equals( lineUpPoints.getFirst().getName(), matchingPoint ) ) {
					lineUpPoints.getFirst().setStart( true );
				} else {
					lineUpPoints.getLast().setStart( true );
				}
				newExit.getPoints().addAll(
					lineUpExitPoints.stream()
						.filter( pointData -> !Objects.equals( pointData.getName(), finalMatchingPoint ) )
						.toList()
				);
				newRoutes.add( newExit );
				lineUp.setFound( true );
			} else {
				errors.add( String.format(
					"Exit \"%s\" and LineUp \"%s\" have no matching point.",
					exit.getRaw(),
					lineUp.getRaw()
				) );
			}
		}
		if( matchingPoint == null ) {
			errors.add( String.format(
				"Exit \"%s\" and its LineUps do not have a matching point.",
				exit.getRaw()
			) );
		} else {
			List<PointData> exitPoints = exit.getPoints();
			List<PointData> holdingPointPoints =
				exit.getPoints().stream()
					.map( pointData -> pointData.toBuilder()
						.start( false )
						.build() )
					.toList();
			if( Objects.equals( matchingPoint, exitPoints.getFirst().getName() ) ) {
				holdingPointPoints.getLast().setStart( true );
			} else {
				holdingPointPoints.getFirst().setStart( true );
			}
			newRoutes.add(
				HoldingPoint.builder()
					.raw( exit.getRaw() )
					.name( exit.getName() )
					.lineUpRunways( exit.getLineUpRunways() )
					.speed( exit.getSpeed() )
					.points( holdingPointPoints )
					.build()
			);
		}
		return newRoutes;
	}
}
