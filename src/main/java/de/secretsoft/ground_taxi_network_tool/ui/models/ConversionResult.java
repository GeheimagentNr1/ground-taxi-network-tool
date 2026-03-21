package de.secretsoft.ground_taxi_network_tool.ui.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ConversionResult {


	private byte[] processedKml;

	private byte[] groundLayoutText;

	private List<String> errors;
}
