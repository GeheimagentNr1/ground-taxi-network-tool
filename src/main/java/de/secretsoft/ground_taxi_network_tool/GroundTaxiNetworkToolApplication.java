package de.secretsoft.ground_taxi_network_tool;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Push
@ColorScheme(ColorScheme.Value.DARK)
@StyleSheet( Lumo.STYLESHEET )
@SpringBootApplication
public class GroundTaxiNetworkToolApplication implements AppShellConfigurator {


	public static void main( String[] args ) {

		SpringApplication.run( GroundTaxiNetworkToolApplication.class, args );
	}
}
