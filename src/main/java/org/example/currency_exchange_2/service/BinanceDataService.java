package org.example.currency_exchange_2.service;

import org.example.currency_exchange_2.domain.MarketData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.example.currency_exchange_2.domain.Klines;

@Service
public class BinanceDataService {

  private final String apiBaseUrl;
  private final RestTemplate restTemplate;

  @Autowired
  public BinanceDataService(@Value("${binance.api.base-url}") String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
    this.restTemplate = new RestTemplate();
  }


  //TODO: Create an interface for data service with functions fetchKlines, getSymbol
  public Klines fetchKlines(MarketData inputData) {
    String symbol = inputData.getBase()+"U"+inputData.getQuote();
    long startTime = inputData.getStartTime();
    long endTime = inputData.getEndTime();

    //TODO: make url in application properties
    String url = String.format("%s/api/v3/klines?symbol=%s&interval=1m&startTime=%s&endTime=%s&limit=60",
            apiBaseUrl, symbol, startTime, endTime);

    try {
      Object[][] response = restTemplate.getForObject(url, Object[][].class);
      return new Klines(response);
    } catch (Exception e) {
      System.err.println("Error fetching klines data: " + e.getMessage());
      return null; //TODO: add
    }
  }
}