package org.example.currency_exchange_2.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.service.MarketDataValidationService;
import org.example.currency_exchange_2.service.BinanceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.DeleteApi;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.example.currency_exchange_2.domain.Klines;


import java.time.OffsetDateTime;



@RestController
@RequestMapping("/api/v0")
@Validated
public class CurrencyMarketDataController {

  @Autowired
  private BinanceDataService service;
  private MarketDataValidationService validationService;

  //TODO: Separate Influx to a separate service
  @Value("${influx.url}")
  private String influxUrl;

  @Value("${influx.token}")
  private String influxToken;

  @Value("${influx.org}")
  private String influxOrg;

  @Value("${influx.bucket}")
  private String influxBucket;

  private WriteApiBlocking writeApi;
  private DeleteApi deleteApi;

  @PostConstruct
  public void PostConstruct() {
    if (applicationPropertiesMissing()) {
      System.err.println("application properties not defined");
      return;
    }
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
    writeApi = influxDBClient.getWriteApiBlocking();
    deleteApi = influxDBClient.getDeleteApi();
    validationService = new MarketDataValidationService();

  }

  public boolean applicationPropertiesMissing() {
    return influxUrl == null || influxBucket == null || influxOrg == null || influxToken == null;
  }

  @Measurement(name = "Klines data")
  private static class KlinesData {
    @Column
    String base;

    @Column
    String quote;

    @Column
    long openTime;

    @Column
    long closeTime;

    @Column
    int numberOfTrades;

    @Column
    String openPrice;

    @Column
    String closePrice;

    @Column
    String highPrice;

    @Column
    String lowPrice;

    @Column
    String volume;

    @Column
    String assetVolume;

    @Column
    String takerBuyBaseAssetVolume;

    @Column
    String takerBuyQuoteAssetVolume;
  }

  // POST /api/v0
  @PostMapping()
  public ResponseEntity<String> postMarketData(@Valid @NotNull MarketData inputData) {
    validationService.checkData(inputData);
    Klines klines = service.fetchKlines(inputData);
    KlinesData klinesData = new KlinesData();
    klinesData.base = inputData.getBase();
    klinesData.quote = inputData.getQuote();
    klinesData.openTime = klines.getOpenTime();
    klinesData.closeTime = klines.getCloseTime();
    klinesData.numberOfTrades = klines.getNumberOfTrades();
    klinesData.openPrice = klines.getOpenPrice();
    klinesData.closePrice = klines.getClosePrice();
    klinesData.highPrice = klines.getHighPrice();
    klinesData.lowPrice = klines.getLowPrice();
    klinesData.volume = klines.getVolume();
    klinesData.assetVolume = klines.getAssetVolume();
    klinesData.takerBuyBaseAssetVolume = klines.getTakerBuyBaseAssetVolume();
    klinesData.takerBuyQuoteAssetVolume = klines.getTakerBuyQuoteAssetVolume();
    writeApi.writeMeasurement(WritePrecision.NS, klinesData);
    return ResponseEntity.ok("Success");
  }

  //TODO: add Get function that takes in exchangeId and returns Klines

  //TODO: remove the delete functions
  @DeleteMapping("column")
  public ResponseEntity<String> deleteThisColumn(String symbol) {
    try {
      String predicate = String.format("symbol=\"%s\"", symbol);

      deleteApi.delete(
              OffsetDateTime.parse("1970-01-01T00:00:00Z"),
              OffsetDateTime.parse("2030-01-01T00:00:00Z"),
              predicate,
              influxBucket,
              influxOrg
      );
      return ResponseEntity.ok(symbol + " deleted successfully");
    } catch (Exception e) {
      throw new RuntimeException("Failed to clear symbol data", e);
    }
  }

  @DeleteMapping("all")
  public ResponseEntity<String> clearAllData() {
    try {
      deleteApi.delete(
              OffsetDateTime.parse("1970-01-01T00:00:00Z"),
              OffsetDateTime.parse("2030-01-01T00:00:00Z"),
              "",
              influxBucket,
              influxOrg
      );
      return ResponseEntity.ok("All Data cleared");
    } catch (Exception e) {
      throw new RuntimeException("Failed to clear symbol data", e);
    }
  }
}