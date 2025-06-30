package org.example.currency_exchange_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.currency_exchange_2.domain.Klines;
import org.example.currency_exchange_2.service.BinanceDataService;

@RestController
@RequestMapping("/api/v0")
public class CurrencyMarketDataController {

  @Autowired
  private BinanceDataService service;

  // POST /api/v0
  @PostMapping()
  public ResponseEntity<String> postMarketData(){
    try {
      Klines klines = service.fetchKlines();
      if (klines != null) {
        return ResponseEntity.ok("Market data fetched and processed successfully");
      } else {
        return ResponseEntity.internalServerError().body("Failed to fetch market data");
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
    }
  }

  @GetMapping()
  public ResponseEntity<Klines> getMarketData() {
    try {
      Klines klines = service.fetchKlines();
      if (klines != null) {
        return ResponseEntity.ok(klines);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}