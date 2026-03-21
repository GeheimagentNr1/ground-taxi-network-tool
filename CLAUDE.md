# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot application with a Vaadin web UI that processes airport ground taxi networks for Euroscope ATC simulation. Supports two conversion directions via file upload/download in the browser:
- **KML to Ground Layout** — Parse KML placemarks, validate, and produce processed KML + Euroscope ground layout text
- **Ground Layout to KML** — Parse Euroscope ground layout text and produce KML

Also includes Spring Batch jobs for the same conversions (file-based, configured via `config/application.yml`).

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

Vaadin-based UI (`MainView`) with two tabs for file upload/download conversions. Uses `NetworkConversionService` which orchestrates the same processing services as the batch pipeline but in-memory (byte[] in, byte[] out).

### Batch Processing Pipeline

Spring Batch job with 9 sequential steps defined in `NetworkBuilderJobConfiguration` (file-based, configured via `config/application.yml`):

1. **ReadKml** — Parse KML placemarks into route objects via regex patterns
2. **BuildExits** — Convert Exit + LineUp definitions into Exit + HoldingPoint pairs
3. **AddGateAndTaxioutPoints** — Add connecting points to gates/stands
4. **ConnectGatesAndTaxiouts** — Link taxiout routes to stands
5. **ConnectCrossings** — Align named crossing points across routes to same coordinates
6. **SortCoordinates** — Order coordinates along each route path
7. **PrintErrors** — Log accumulated validation errors
8. **WriteKml** — Output processed KML
9. **WriteGroundLayout** — Generate Euroscope ground layout text file

A second batch job exists under `job/ground_layout_to_kml/` for the reverse conversion.

### Key Components

- **NetworkConversionService** — Stateless service used by the web UI. Orchestrates the full KML-to-ground-layout and ground-layout-to-KML conversions in-memory.
- **RoutesHolder** — Singleton service that accumulates routes and errors across batch pipeline steps. Central state container for the batch jobs.
- **RouteData** — Polymorphic interface (with Jackson `@JsonSubTypes`) implemented by: Exit, LineUp, HoldingPoint, Stand, Taxiway, Taxiout. Each type knows how to serialize itself to ground layout format.
- **KmlLoader** — Parses KML placemark descriptions using regex patterns (LINE_UP_PATTERN, EXIT_PATTERN, STAND_PATTERN, etc.). Default speeds: 20 knots (taxiways), 10 knots (stands/gates/taxiouts).
- **LatLngConverter** — Converts between decimal degrees and DMS format for ground layout output.

### Adding a New Pipeline Step

1. Create step configuration in `job/steps/{step_name}/`
2. Inject and add the step in `NetworkBuilderJobConfiguration`

### Data Format

Input KML placemarks use a semicolon-delimited description format (see NOTES.md for full spec):
- `Exit; <LineUpRunways>; <ExitRunway>-<LEFT|RIGHT>; <Name>; <RoutePoints>; [<Speed>]`
- `LU; <Runway>; <ExitRunway>; <IntersectionName>; <RoutePoints>; [<Speed>]`
- `Stand; <Name>; <RoutePoints>; [<Taxiout 0/1>]; [<Speed>]`
- `Taxiout; <Name>; <StandName>; <RoutePoints>; [<Speed>]`
- Taxiways have no type prefix: `<Name>; <RoutePoints>; [<HasGates 0/1>]; [<Speed>]`

Route points are hyphen-separated. Underscore-prefixed points are non-crossing intermediates.

### Configuration

File paths configured in `config/application.yml` under `app.*` prefix (inputKmlFile, processedKmlFile, groundLayoutFile). Airport data lives in `data/{ICAO}/`.

### Testing

JUnit 5 with JUnit Pioneer for JSON-parameterized tests. Test data files in `src/test/resources/`. All models use Lombok `@Builder` for test construction.

### Tech Stack

Java 21, Spring Boot 4.0.4, Spring Batch, Vaadin 25, JAXB (KML parsing via XSD-generated classes), Jackson, H2 (in-memory for Batch metadata), Lombok, Maven.
