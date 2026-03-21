package de.secretsoft.ground_taxi_network_tool.services;

import de.secretsoft.ground_taxi_network_tool.models.PointData;
import de.secretsoft.ground_taxi_network_tool.models.RouteData;
import de.secretsoft.ground_taxi_network_tool.models.Taxiway;
import de.secretsoft.ground_taxi_network_tool.ui.models.ConversionResult;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.opengis.kml._2.Document;
import net.opengis.kml._2.Kml;
import net.opengis.kml._2.Placemark;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class NetworkConversionService {


	private final KmlLoader kmlLoader;

	private final ExitBuilder exitBuilder;

	private final GateAndTaxioutPointAdder gateAndTaxioutPointAdder;

	private final GateTaxioutConnector gateTaxioutConnector;

	private final CrossingConnector crossingConnector;

	private final CoordinatesSorter coordinatesSorter;

	private final KmlWriter kmlWriter;

	private final LatLngConverter latLngConverter;

	public ConversionResult convertKmlToGroundLayout( byte[] kmlContent, String fileName ) {

		List<RouteData> routes = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		List<Placemark> placemarks = parseKml( kmlContent );
		for( Placemark placemark : placemarks ) {
			Pair<RouteData, List<String>> result = kmlLoader.placemarkToRouteData( placemark );
			if( result.getLeft() != null ) {
				routes.add( result.getLeft() );
			}
			errors.addAll( result.getRight() );
		}

		errors.addAll( exitBuilder.processExits( routes ) );
		errors.addAll( gateAndTaxioutPointAdder.addGatePoints( routes ) );
		errors.addAll( gateTaxioutConnector.connectGatesAndTaxiouts( routes ) );
		errors.addAll( crossingConnector.connectCrossings( routes ) );
		coordinatesSorter.sort( routes );

		String baseName = fileName.replaceFirst( "\\.[^.]+$", "" );

		byte[] processedKml = marshalKml( kmlWriter.routesToKmlDocument( baseName + "_processed.kml", routes ) );

		String groundLayoutText = routes.stream()
			.flatMap( route -> route.buildGroundLayoutStrings( latLngConverter ).stream() )
			.collect( Collectors.joining( "\r\n" ) );

		return ConversionResult.builder()
			.processedKml( processedKml )
			.groundLayoutText( groundLayoutText.getBytes( StandardCharsets.UTF_8 ) )
			.errors( errors )
			.build();
	}

	public ConversionResult convertGroundLayoutToKml( byte[] content, String fileName ) {

		List<RouteData> routes = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		String text = new String( content, StandardCharsets.UTF_8 );
		try( BufferedReader reader = new BufferedReader( new StringReader( text ) ) ) {
			RouteData currentRoute = null;
			String line;
			while( ( line = reader.readLine() ) != null ) {
				if( line.isEmpty() || line.charAt( 0 ) == ';' || line.startsWith( "[GROUND]" ) ) {
					continue;
				}
				int commentIndex = line.indexOf( ';' );
				if( commentIndex != -1 ) {
					line = line.substring( 0, commentIndex ).trim();
				}
				if( line.startsWith( "COORD" ) ) {
					if( currentRoute != null ) {
						List<Integer> pointIndexes = new ArrayList<>();
						List<Integer> doublePointIndexes = new ArrayList<>();
						for( int i = 0; i < line.length(); i++ ) {
							if( line.charAt( i ) == '.' ) {
								pointIndexes.add( i );
							}
							if( line.charAt( i ) == ':' ) {
								doublePointIndexes.add( i );
							}
						}
						if( pointIndexes.size() == 6 ) {
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
							errors.add( String.format( "Invalid COORD line: \"%s\"", line ) );
						}
					}
				} else {
					currentRoute = Taxiway.builder()
						.raw( line )
						.name( line.contains( ":" ) ? line.split( ":" )[1] : line )
						.hasStandsTaxiouts( false )
						.speed( 0 )
						.points( new ArrayList<>() )
						.build();
					routes.add( currentRoute );
				}
			}
		} catch( Exception e ) {
			errors.add( "Error parsing ground layout: " + e.getMessage() );
		}

		if( !routes.isEmpty() ) {
			routes.forEach( route -> {
				if( !route.getPoints().isEmpty() ) {
					route.getPoints().getFirst().setStart( true );
				}
			} );
		}

		String baseName = fileName.replaceFirst( "\\.[^.]+$", "" );
		byte[] kmlBytes = marshalKml( kmlWriter.routesToKmlDocument( baseName + ".kml", routes ) );

		return ConversionResult.builder()
			.processedKml( kmlBytes )
			.errors( errors )
			.build();
	}

	private List<Placemark> parseKml( byte[] kmlContent ) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document domDoc = builder.parse( new ByteArrayInputStream( kmlContent ) );

			NodeList placemarkNodes = domDoc.getElementsByTagNameNS( "http://www.opengis.net/kml/2.2", "Placemark" );

			JAXBContext context = JAXBContext.newInstance( Placemark.class );
			Unmarshaller unmarshaller = context.createUnmarshaller();

			List<Placemark> placemarks = new ArrayList<>();
			for( int i = 0; i < placemarkNodes.getLength(); i++ ) {
				Element element = (Element) placemarkNodes.item( i );
				JAXBElement<Placemark> jaxbElement = unmarshaller.unmarshal( element, Placemark.class );
				placemarks.add( jaxbElement.getValue() );
			}
			return placemarks;
		} catch( Exception e ) {
			throw new RuntimeException( "Failed to parse KML file: " + e.getMessage(), e );
		}
	}

	private byte[] marshalKml( Document document ) {

		try {
			Kml kml = new Kml();
			kml.setDocument( document );

			JAXBContext context = JAXBContext.newInstance( Kml.class );
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			marshaller.marshal( kml, outputStream );
			return outputStream.toByteArray();
		} catch( Exception e ) {
			throw new RuntimeException( "Failed to marshal KML: " + e.getMessage(), e );
		}
	}
}
