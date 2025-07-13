package org.example.currency_exchange_2.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.domain.exception.DataNotFoundException;
import org.example.currency_exchange_2.service.MarketDataValidationService;
import org.example.currency_exchange_2.service.BinanceDataService;
import org.example.currency_exchange_2.service.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.example.currency_exchange_2.domain.Klines;

import java.util.ArrayList;
import java.util.List;

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
    klinesData.exchangeId = inputData.getExchangeId().toString();
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

  @GetMapping("/{exchangeId}")
  public ResponseEntity<Klines> getKlines(@PathVariable Integer exchangeId) {
    List<InfluxDBService.KlinesData> recievedKlines = influxDBService.queryKlinesData(exchangeId);
    if (recievedKlines.isEmpty()) {
      throw new DataNotFoundException("Not data matches current exchangeId");
    }
    InfluxDBService.KlinesData firstKline = recievedKlines.get(0);
    Klines data = new Klines();
    data.setExchangeId(Integer.parseInt(firstKline.exchangeId));
    data.setOpenTime(firstKline.openTime);
    data.setCloseTime(firstKline.closeTime);
    data.setNumberOfTrades(firstKline.numberOfTrades);
    data.setOpenPrice(firstKline.openPrice);
    data.setClosePrice(firstKline.closePrice);
    data.setHighPrice(firstKline.highPrice);
    data.setLowPrice(firstKline.lowPrice);
    data.setVolume(firstKline.volume);
    data.setAssetVolume(firstKline.assetVolume);
    data.setTakerBuyBaseAssetVolume(firstKline.takerBuyBaseAssetVolume);
    data.setTakerBuyQuoteAssetVolume(firstKline.takerBuyQuoteAssetVolume);

    return ResponseEntity.ok(data);
  }


  //Delete function used for testing, development purposes
  @DeleteMapping("/column")
  public ResponseEntity<String> deleteThisColumn(String symbol) {
    try {
      influxDBService.deleteBySymbol(symbol);
      return ResponseEntity.ok(symbol + " deleted successfully");
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete symbol data", e);
    }
  }

  @DeleteMapping("/all")
  public ResponseEntity<String> clearAllData() {
    try {
      influxDBService.clearAllData();
      return ResponseEntity.ok("All Data cleared");
    } catch (Exception e) {
      throw new RuntimeException("Failed to clear all data", e);
    }
  }
}