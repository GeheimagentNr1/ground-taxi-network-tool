package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import net.opengis.kml._2.Document;
import net.opengis.kml._2.LineString;
import net.opengis.kml._2.Placemark;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KmlWriter {
	
	
	public Document routesToKmlDocument( String name, List<RouteData> routes ) {
		
		Document document = new Document();
		
		document.setName( name );
		document.getPlacemarks().addAll(
			routes.stream()
				.map( this::routeToPlacemark )
				.toList()
		);
		return document;
	}
	
	private Placemark routeToPlacemark( RouteData route ) {
		
		Placemark placemark = new Placemark();
		placemark.setName( route.buildKmlProcessedRawString() );
		LineString lineString = new LineString();
		lineString.getCoordinates().addAll(
			route.getPoints().stream()
				.map( PointData::buildKmlCoordinatesString )
				.toList()
		);
		placemark.setLineString( lineString );
		return placemark;
	}
}
