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
  private final String klinesUrlPattern;
  private final RestTemplate restTemplate;

  @Autowired
  public BinanceDataService(
          @Value("${binance.api.base-url}") String apiBaseUrl,
          @Value("${binance.api.klines-url}") String klinesUrlPattern) {
    this.apiBaseUrl = apiBaseUrl;
    this.klinesUrlPattern = klinesUrlPattern;
    this.restTemplate = new RestTemplate();
  }

  //TODO: Add springboot retry dependency
  public Klines fetchKlines(MarketData inputData) {
    String symbol = inputData.getBase() + inputData.getQuote();
    long startTime = inputData.getStartTime();
    long endTime = inputData.getEndTime();

    //TODO: If the endtime - starttime interval is greater than 500, output the entire list

    // Use String.format with the injected pattern
    String url = String.format(klinesUrlPattern, apiBaseUrl, symbol, startTime, endTime);
    try {
      Object[][] response = restTemplate.getForObject(url, Object[][].class);
      if (response == null || response.length == 0) {
        System.err.println("No data returned from Binance API");
        return null;
      }
      return new Klines(inputData.getExchangeId(), response);
    } catch (Exception e) {
      //TODO: throw an error instead of printing it out
      System.err.println("Error fetching klines data: " + e.getMessage());
      e.printStackTrace(); // More detailed error info
      return null;
    }
  }
}