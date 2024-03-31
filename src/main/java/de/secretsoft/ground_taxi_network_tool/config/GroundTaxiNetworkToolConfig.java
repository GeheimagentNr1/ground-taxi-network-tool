package de.secretsoft.ground_taxi_network_tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;


@Data
@Configuration
@ConfigurationProperties( prefix = "app" )
public class GroundTaxiNetworkToolConfig {
	
	
	private Path inputKmlFile;
	
	private Path processedKmlFile;
	
	private Path groundLayoutFile;
}
