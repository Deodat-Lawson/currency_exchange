package org.example.currency_exchange_2.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.service.MarketDataValidationService;
import org.example.currency_exchange_2.service.BinanceDataService;
import org.example.currency_exchange_2.service.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import org.example.currency_exchange_2.domain.Klines;

@RestController
@RequestMapping("/api/v0")
@Validated
public class CurrencyMarketDataController {

  @Autowired
  private BinanceDataService service;

  @Autowired
  private InfluxDBService influxDBService;

  @Autowired
  private MarketDataValidationService validationService;

  @PostMapping()
  public ResponseEntity<String> postMarketData(@Valid @NotNull @RequestBody MarketData inputData) {
    validationService.checkData(inputData);
    Klines klines = service.fetchKlines(inputData);

    InfluxDBService.KlinesData klinesData = new InfluxDBService.KlinesData();
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

    influxDBService.writeKlinesData(klinesData);
    return ResponseEntity.ok("Success");
  }

  //TODO: add Get function that takes in exchangeId and returns Klines

  //Delete function used for testing, development purposes
  @DeleteMapping("column")
  public ResponseEntity<String> deleteThisColumn(String symbol) {
    try {
      influxDBService.deleteBySymbol(symbol);
      return ResponseEntity.ok(symbol + " deleted successfully");
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete symbol data", e);
    }
  }

  @DeleteMapping("all")
  public ResponseEntity<String> clearAllData() {
    try {
      influxDBService.clearAllData();
      return ResponseEntity.ok("All Data cleared");
    } catch (Exception e) {
      throw new RuntimeException("Failed to clear all data", e);
    }
  }
}