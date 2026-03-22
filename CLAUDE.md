# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot application with a Vaadin web UI that processes airport ground taxi networks for Euroscope ATC simulation. Supports two conversion directions via file upload/download in the browser:
- **KML to Ground Layout** — Parse KML placemarks, validate, and produce processed KML + Euroscope ground layout text
- **Ground Layout to KML** — Parse Euroscope ground layout text and produce KML

## Build & Run Commands

```bash
# Build (Java 21 required)
mvn clean install

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ExitBuilderTest

# Run a single test method
mvn test -Dtest=ExitBuilderTest#testMethodName

# Package
mvn clean package

# Run web UI (starts on http://localhost:8080)
java -jar target/ground-taxi-network-tool-1.0.0-SNAPSHOT-bin.jar

# Note: JAVA_HOME must point to Java 21, e.g.:
# export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"
```

## Architecture

### Web UI

Vaadin-based UI (`MainView`) with two tabs for file upload/download conversions. Uses `NetworkConversionService` which orchestrates the processing services in-memory (byte[] in, byte[] out).

### Key Components

- **NetworkConversionService** — Stateless service used by the web UI. Orchestrates the full KML-to-ground-layout and ground-layout-to-KML conversions in-memory.
- **RouteData** — Polymorphic interface (with Jackson `@JsonSubTypes`) implemented by: Exit, LineUp, HoldingPoint, Stand, Taxiway, Taxiout. Each type knows how to serialize itself to ground layout format.
- **KmlLoader** — Parses KML placemark descriptions using regex patterns (LINE_UP_PATTERN, EXIT_PATTERN, STAND_PATTERN, etc.). Default speeds: 20 knots (taxiways), 10 knots (stands/gates/taxiouts).
- **LatLngConverter** — Converts between decimal degrees and DMS format for ground layout output.

### Data Format

Input KML placemarks use a semicolon-delimited description format (see NOTES.md for full spec):
- `Exit; <LineUpRunways>; <ExitRunway>-<LEFT|RIGHT>; <Name>; <RoutePoints>; [<Speed>]`
- `LU; <Runway>; <ExitRunway>; <IntersectionName>; <RoutePoints>; [<Speed>]`
- `Stand; <Name>; <RoutePoints>; [<Taxiout 0/1>]; [<Speed>]`
- `Taxiout; <Name>; <StandName>; <RoutePoints>; [<Speed>]`
- Taxiways have no type prefix: `<Name>; <RoutePoints>; [<HasGates 0/1>]; [<Speed>]`

Route points are hyphen-separated. Underscore-prefixed points are non-crossing intermediates.

### Testing

JUnit 5 with JUnit Pioneer for JSON-parameterized tests. Test data files in `src/test/resources/`. All models use Lombok `@Builder` for test construction.

### Tech Stack

Java 21, Spring Boot 4.0.4, Vaadin 25, JAXB (KML parsing via XSD-generated classes), Jackson, Lombok, Maven.
