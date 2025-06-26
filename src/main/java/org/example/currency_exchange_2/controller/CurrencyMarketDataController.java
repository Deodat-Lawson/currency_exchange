package org.example.currency_exchange_2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0")
public class CurrencyMarketDataController {

  // POST /api/v0
  @PostMapping()
  public void postMarketData(){

  }


  @GetMapping()
  public String getMarketData() {
    return "Getting Market Data...";
  }
}
