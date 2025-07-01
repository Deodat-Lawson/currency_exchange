package org.example.currency_exchange_2;

import org.example.currency_exchange_2.controller.CurrencyMarketDataController;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.domain.Klines;
import org.example.currency_exchange_2.service.BinanceDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.domain.WritePrecision;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyMarketDataControllerTests {

  @Mock
  private BinanceDataService binanceDataService;

  @Mock
  private InfluxDBClient influxDBClient;

  @Mock
  private WriteApiBlocking writeApi;

  @Mock
  private DeleteApi deleteApi;

  @InjectMocks
  private CurrencyMarketDataController controller;

  private MarketData testMarketData;
  private Klines testKlines;

  @BeforeEach
  void setUp() {
    // Set up test data
    testMarketData = new MarketData();
    testMarketData.setBase("BTC");
    testMarketData.setQuote("USDT");

    testKlines = new Klines();
    testKlines.setOpenTime(1640995200000L);
    testKlines.setCloseTime(1640995260000L);
    testKlines.setNumberOfTrades(100);
    testKlines.setOpenPrice("50000.00");
    testKlines.setClosePrice("50100.00");
    testKlines.setHighPrice("50200.00");
    testKlines.setLowPrice("49900.00");
    testKlines.setVolume("10.5");
    testKlines.setAssetVolume("525000.00");
    testKlines.setTakerBuyBaseAssetVolume("5.2");
    testKlines.setTakerBuyQuoteAssetVolume("260000.00");

    // Set up controller properties
    ReflectionTestUtils.setField(controller, "influxUrl", "http://localhost:8086");
    ReflectionTestUtils.setField(controller, "influxToken", "test-token");
    ReflectionTestUtils.setField(controller, "influxOrg", "test-org");
    ReflectionTestUtils.setField(controller, "influxBucket", "test-bucket");
    ReflectionTestUtils.setField(controller, "writeApi", writeApi);
    ReflectionTestUtils.setField(controller, "deleteApi", deleteApi);
  }

  @Test
  void testPostConstruct_WithValidProperties() {
    try (MockedStatic<InfluxDBClientFactory> mockedFactory = mockStatic(InfluxDBClientFactory.class)) {
      when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApi);
      when(influxDBClient.getDeleteApi()).thenReturn(deleteApi);
      mockedFactory.when(() -> InfluxDBClientFactory.create(anyString(), any(char[].class), anyString(), anyString()))
              .thenReturn(influxDBClient);

      controller.PostConstruct();

      mockedFactory.verify(() -> InfluxDBClientFactory.create(
              eq("http://localhost:8086"),
              any(char[].class),
              eq("test-org"),
              eq("test-bucket")
      ));
    }
  }

  @Test
  void testPostConstruct_WithMissingProperties() {
    ReflectionTestUtils.setField(controller, "influxUrl", null);

    assertDoesNotThrow(() -> controller.PostConstruct());
  }

  @Test
  void testApplicationPropertiesMissing_AllPropertiesPresent() {
    boolean result = controller.applicationPropertiesMissing();
    assertFalse(result);
  }

  @Test
  void testApplicationPropertiesMissing_UrlMissing() {
    ReflectionTestUtils.setField(controller, "influxUrl", null);
    boolean result = controller.applicationPropertiesMissing();
    assertTrue(result);
  }

  @Test
  void testApplicationPropertiesMissing_TokenMissing() {
    ReflectionTestUtils.setField(controller, "influxToken", null);
    boolean result = controller.applicationPropertiesMissing();
    assertTrue(result);
  }

  @Test
  void testApplicationPropertiesMissing_OrgMissing() {
    ReflectionTestUtils.setField(controller, "influxOrg", null);
    boolean result = controller.applicationPropertiesMissing();
    assertTrue(result);
  }

  @Test
  void testApplicationPropertiesMissing_BucketMissing() {
    ReflectionTestUtils.setField(controller, "influxBucket", null);
    boolean result = controller.applicationPropertiesMissing();
    assertTrue(result);
  }

  @Test
  void testPostMarketData_Success() throws Exception {
    // Arrange
    when(binanceDataService.fetchKlines(testMarketData)).thenReturn(testKlines);
    doNothing().when(writeApi).writeMeasurement(eq(WritePrecision.NS), any(Object.class));

    // Act
    ResponseEntity<String> response = controller.postMarketData(testMarketData);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Success", response.getBody());

    verify(binanceDataService).fetchKlines(testMarketData);
    verify(writeApi).writeMeasurement(eq(WritePrecision.NS), any(Object.class));
  }

  @Test
  void testPostMarketData_ServiceThrowsException() throws Exception {
    // Arrange
    when(binanceDataService.fetchKlines(testMarketData))
            .thenThrow(new RuntimeException("Binance API error"));

    // Act
    ResponseEntity<String> response = controller.postMarketData(testMarketData);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Error: Binance API error"));

    verify(binanceDataService).fetchKlines(testMarketData);
    verify(writeApi, never()).writeMeasurement(any(WritePrecision.class), any(Object.class));
  }

  @Test
  void testPostMarketData_InfluxWriteThrowsException() throws Exception {
    // Arrange
    when(binanceDataService.fetchKlines(testMarketData)).thenReturn(testKlines);
    doThrow(new RuntimeException("InfluxDB write error"))
            .when(writeApi).writeMeasurement(eq(WritePrecision.NS), any(Object.class));

    // Act
    ResponseEntity<String> response = controller.postMarketData(testMarketData);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Error: InfluxDB write error"));

    verify(binanceDataService).fetchKlines(testMarketData);
    verify(writeApi).writeMeasurement(eq(WritePrecision.NS), any(Object.class));
  }

  @Test
  void testDeleteThisColumn_Success() throws Exception {
    // Arrange
    String symbol = "BTCUSDT";
    doNothing().when(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );

    // Act
    ResponseEntity<String> response = controller.deleteThisColumn(symbol);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("BTCUSDT deleted successfully", response.getBody());

    verify(deleteApi).delete(
            eq(OffsetDateTime.parse("1970-01-01T00:00:00Z")),
            eq(OffsetDateTime.parse("2030-01-01T00:00:00Z")),
            eq("symbol=\"BTCUSDT\""),
            eq("test-bucket"),
            eq("test-org")
    );
  }

  @Test
  void testDeleteThisColumn_ThrowsException() throws Exception {
    // Arrange
    String symbol = "BTCUSDT";
    doThrow(new RuntimeException("Delete operation failed"))
            .when(deleteApi).delete(
                    any(OffsetDateTime.class),
                    any(OffsetDateTime.class),
                    anyString(),
                    anyString(),
                    anyString()
            );

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> controller.deleteThisColumn(symbol));

    assertEquals("Failed to clear symbol data", exception.getMessage());
    assertTrue(exception.getCause().getMessage().contains("Delete operation failed"));

    verify(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );
  }

  @Test
  void testClearAllData_Success() throws Exception {
    // Arrange
    doNothing().when(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );

    // Act
    ResponseEntity<String> response = controller.clearAllData();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("All Data cleared", response.getBody());

    verify(deleteApi).delete(
            eq(OffsetDateTime.parse("1970-01-01T00:00:00Z")),
            eq(OffsetDateTime.parse("2030-01-01T00:00:00Z")),
            eq(""),
            eq("test-bucket"),
            eq("test-org")
    );
  }

  @Test
  void testClearAllData_ThrowsException() throws Exception {
    // Arrange
    doThrow(new RuntimeException("Clear all operation failed"))
            .when(deleteApi).delete(
                    any(OffsetDateTime.class),
                    any(OffsetDateTime.class),
                    anyString(),
                    anyString(),
                    anyString()
            );

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> controller.clearAllData());

    assertEquals("Failed to clear symbol data", exception.getMessage());
    assertTrue(exception.getCause().getMessage().contains("Clear all operation failed"));

    verify(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );
  }

  @Test
  void testPostMarketData_WithNullInput() {
    // Act
    ResponseEntity<String> response = controller.postMarketData(null);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Error:"));
  }

  @Test
  void testDeleteThisColumn_WithNullSymbol() throws Exception {
    // Arrange
    doNothing().when(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );

    // Act
    ResponseEntity<String> response = controller.deleteThisColumn(null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("null deleted successfully", response.getBody());

    verify(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            eq("symbol=\"null\""),
            anyString(),
            anyString()
    );
  }

  @Test
  void testDeleteThisColumn_WithEmptySymbol() throws Exception {
    // Arrange
    String emptySymbol = "";
    doNothing().when(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            anyString(),
            anyString(),
            anyString()
    );

    // Act
    ResponseEntity<String> response = controller.deleteThisColumn(emptySymbol);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(" deleted successfully", response.getBody());

    verify(deleteApi).delete(
            any(OffsetDateTime.class),
            any(OffsetDateTime.class),
            eq("symbol=\"\""),
            anyString(),
            anyString()
    );
  }
}