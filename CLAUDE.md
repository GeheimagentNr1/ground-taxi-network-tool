# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot Batch application that processes KML files defining airport ground taxi networks (for Euroscope ATC simulation) and outputs validated/processed KML and Euroscope-compatible ground layout text files.

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

# Run (reads config/application.yml for input/output file paths)
java -jar target/ground-taxi-network-tool-1.0.0-SNAPSHOT-bin.jar
```

## Architecture

### Processing Pipeline

The application runs a Spring Batch job with 9 sequential steps defined in `NetworkBuilderJobConfiguration`:

1. **ReadKml** — Parse KML placemarks into route objects via regex patterns
2. **BuildExits** — Convert Exit + LineUp definitions into Exit + HoldingPoint pairs
3. **AddGateAndTaxioutPoints** — Add connecting points to gates/stands
4. **ConnectGatesAndTaxiouts** — Link taxiout routes to stands
5. **ConnectCrossings** — Align named crossing points across routes to same coordinates
6. **SortCoordinates** — Order coordinates along each route path
7. **PrintErrors** — Log accumulated validation errors
8. **WriteKml** — Output processed KML
9. **WriteGroundLayout** — Generate Euroscope ground layout text file

### Key Components

- **RoutesHolder** — Singleton service that accumulates routes and errors across all pipeline steps. Central state container.
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

Java 21, Spring Boot 3.2.4, Spring Batch, JAXB (KML parsing via XSD-generated classes), Jackson, H2 (in-memory for Batch metadata), Lombok, Maven.
