package de.secretsoft.ground_taxi_network_tool.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import de.secretsoft.ground_taxi_network_tool.services.NetworkConversionService;
import de.secretsoft.ground_taxi_network_tool.ui.models.ConversionResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;


@RequiredArgsConstructor
@PageTitle( "Ground Taxi Network Tool" )
@Route( "/" )
public class MainView extends VerticalLayout {


	private final NetworkConversionService networkConversionService;

	@PostConstruct
	public void init() {

		setSizeFull();
		setPadding( true );
		setSpacing( true );

		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidthFull();

		tabSheet.add( "KML to Ground Layout", createKmlToGroundLayoutTab() );
		tabSheet.add( "Ground Layout to KML", createGroundLayoutToKmlTab() );

		add( tabSheet );
	}

	private VerticalLayout createKmlToGroundLayoutTab() {

		VerticalLayout layout = new VerticalLayout();
		layout.setPadding( false );
		layout.setSpacing( true );

		Span uploadLabel = new Span( "Upload KML File" );
		uploadLabel.getStyle().set( "font-weight", "bold" ).set( "font-size", "var(--lumo-font-size-l)" );

		TextArea errorArea = new TextArea( "Errors" );
		errorArea.setWidthFull();
		errorArea.setReadOnly( true );
		errorArea.setVisible( false );
		errorArea.setMinHeight( "100px" );
		errorArea.setMaxHeight( "300px" );

		VerticalLayout downloadSection = new VerticalLayout();
		downloadSection.setPadding( false );
		downloadSection.setSpacing( true );
		downloadSection.setVisible( false );

		UI ui = UI.getCurrent();

		Upload upload = createUpload( UploadHandler.inMemory( ( metadata, data ) -> {
			ui.access( () -> {
				errorArea.setVisible( false );
				downloadSection.setVisible( false );
				downloadSection.removeAll();
			} );

			try {
				ConversionResult result = networkConversionService.convertKmlToGroundLayout( data, metadata.fileName() );
				ui.access( () -> {
					if( !result.getErrors().isEmpty() ) {
						errorArea.setValue( String.join( "\n", result.getErrors() ) );
						errorArea.setVisible( true );
					}

					String baseName = metadata.fileName().replaceFirst( "\\.[^.]+$", "" );

					Span downloadHeader = new Span( "Downloads" );
					downloadHeader.getStyle().set( "font-weight", "bold" ).set( "font-size", "var(--lumo-font-size-l)" );
					downloadSection.add( downloadHeader );

					if( result.getProcessedKml() != null ) {
						byte[] kmlBytes = result.getProcessedKml();
						Anchor kmlDownload = new Anchor(
							DownloadHandler.fromInputStream(
								downloadEvent -> new DownloadResponse(
									new ByteArrayInputStream( kmlBytes ),
									baseName + "_processed.kml",
									"application/vnd.google-earth.kml+xml",
									kmlBytes.length
								)
							),
							"Download Processed KML"
						);
						downloadSection.add( kmlDownload );
					}

					if( result.getGroundLayoutText() != null ) {
						byte[] txtBytes = result.getGroundLayoutText();
						Anchor txtDownload = new Anchor(
							DownloadHandler.fromInputStream(
								downloadEvent -> new DownloadResponse(
									new ByteArrayInputStream( txtBytes ),
									baseName + "_ground_layout.txt",
									"text/plain",
									txtBytes.length
								)
							),
							"Download Ground Layout"
						);
						downloadSection.add( txtDownload );
					}

					downloadSection.setVisible( true );
					Notification.show( "Processing completed!" );
				} );
			} catch( Exception exception ) {
				ui.access( () -> {
					errorArea.setValue( "Error: " + exception.getMessage() );
					errorArea.setVisible( true );
					Notification.show( "Error: " + exception.getMessage() );
				} );
			}
		} ) );
		upload.setAcceptedFileTypes( ".kml" );

		layout.add( uploadLabel, upload, errorArea, downloadSection );

		return layout;
	}

	private VerticalLayout createGroundLayoutToKmlTab() {

		VerticalLayout layout = new VerticalLayout();
		layout.setPadding( false );
		layout.setSpacing( true );

		Span uploadLabel = new Span( "Upload Ground Layout File" );
		uploadLabel.getStyle().set( "font-weight", "bold" ).set( "font-size", "var(--lumo-font-size-l)" );

		TextArea errorArea = new TextArea( "Errors" );
		errorArea.setWidthFull();
		errorArea.setReadOnly( true );
		errorArea.setVisible( false );
		errorArea.setMinHeight( "100px" );
		errorArea.setMaxHeight( "300px" );

		VerticalLayout downloadSection = new VerticalLayout();
		downloadSection.setPadding( false );
		downloadSection.setSpacing( true );
		downloadSection.setVisible( false );

		UI ui = UI.getCurrent();

		Upload upload = createUpload( UploadHandler.inMemory( ( metadata, data ) -> {
			ui.access( () -> {
				errorArea.setVisible( false );
				downloadSection.setVisible( false );
				downloadSection.removeAll();
			} );

			try {
				ConversionResult result = networkConversionService.convertGroundLayoutToKml( data, metadata.fileName() );
				ui.access( () -> {
					if( !result.getErrors().isEmpty() ) {
						errorArea.setValue( String.join( "\n", result.getErrors() ) );
						errorArea.setVisible( true );
					}

					String baseName = metadata.fileName().replaceFirst( "\\.[^.]+$", "" );

					Span downloadHeader = new Span( "Downloads" );
					downloadHeader.getStyle().set( "font-weight", "bold" ).set( "font-size", "var(--lumo-font-size-l)" );
					downloadSection.add( downloadHeader );

					if( result.getProcessedKml() != null ) {
						byte[] kmlBytes = result.getProcessedKml();
						Anchor kmlDownload = new Anchor(
							DownloadHandler.fromInputStream(
								downloadEvent -> new DownloadResponse(
									new ByteArrayInputStream( kmlBytes ),
									baseName + ".kml",
									"application/vnd.google-earth.kml+xml",
									kmlBytes.length
								)
							),
							"Download KML"
						);
						downloadSection.add( kmlDownload );
					}

					downloadSection.setVisible( true );
					Notification.show( "Processing completed!" );
				} );
			} catch( Exception exception ) {
				ui.access( () -> {
					errorArea.setValue( "Error: " + exception.getMessage() );
					errorArea.setVisible( true );
					Notification.show( "Error: " + exception.getMessage() );
				} );
			}
		} ) );
		upload.setAcceptedFileTypes( ".txt" );

		layout.add( uploadLabel, upload, errorArea, downloadSection );

		return layout;
	}

	private Upload createUpload( UploadHandler handler ) {

		Upload upload = new Upload( handler );
		upload.setMaxFiles( 1 );
		return upload;
	}
}
