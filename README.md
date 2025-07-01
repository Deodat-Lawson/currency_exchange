# Currency Exchange Application

A Spring Boot application for tracking cryptocurrency exchange rates with InfluxDB integration.

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Binance API access

## Installation

### 1. Install InfluxDB 2 OSS

See documentation for [InfluxDB 2 OSS](https://docs.influxdata.com/influxdb/v2.7/install/) for detailed installation instructions.

**Note:**
Make sure to set up your InfluxDB instance and create a bucket for storing cryptocurrency time series data. Ensure you can open influxdb at port localhost:8086.

### 2. Application Configuration

Create an `application.properties` file in your project's `src/main/resources/` directory:

```properties
# Application Configuration
spring.application.name=currency-exchange-app

# Binance API Configuration
binance.api.base-url=https://api.binance.com

# InfluxDB Configuration
influx.url=http://localhost:8086
influx.token=YOUR_INFLUX_DB_TOKEN
influx.org=YOUR_INFLUX_DB_ORG
influx.bucket=YOUR_INFLUX_DB_BUCKET
```
 
**Configuration Parameters:**
- `spring.application.name`: Your application identifier
- `binance.api.base-url`: Binance API endpoint (usually `https://api.binance.com`)
- `influx.url`: Your InfluxDB server URL
- `influx.token`: InfluxDB API token (generated during setup)
- `influx.org`: Your InfluxDB organization name
- `influx.bucket`: Target bucket for storing exchange data

### 3. Running the Application

#### Using IDE
1. Open the project in your preferred IDE
2. Navigate to `src/main/java/org/example/CurrencyExchange2Application.java`
3. Run the main class

#### Using Command Line
```bash
# Navigate to project directory
cd /path/to/your/project

# Run with Maven
./mvnw spring-boot:run

# Or run with Gradle
./gradlew bootRun

# Or run the compiled JAR
java -jar target/currency-exchange-app.jar
```

## Verification

Once running, the application will:
- Connect to your InfluxDB instance
- Start fetching cryptocurrency data from Binance
- Store the data in your configured InfluxDB bucket

Check your InfluxDB interface at `http://localhost:8086` to verify data is being stored correctly.

## Troubleshooting

- **Connection Issues**: Verify InfluxDB is running and accessible
- **API Errors**: Check your Binance API configuration and network connectivity
- **Permission Issues**: Ensure your InfluxDB token has write permissions to the specified bucket