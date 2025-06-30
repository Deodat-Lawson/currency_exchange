package org.example.currency_exchange_2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.example.currency_exchange_2.domain.Klines;

import java.util.List;

@Service
public class BinanceDataService {

  @Value("${data.symbols}")
  private String symbolsConfig;

  private final String apiBaseUrl;
  private final RestTemplate restTemplate;

  @Autowired
  public BinanceDataService(@Value("${binance.api.base-url}") String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
    this.restTemplate = new RestTemplate();
  }

  public Klines fetchKlines() {
    String symbol = symbolsConfig.split(",")[0].trim();

    String url = String.format("%s/api/v3/klines?symbol=%s&interval=1m&limit=60",
            apiBaseUrl, symbol);

    try {
      Object[][] response = restTemplate.getForObject(url, Object[][].class);
      return new Klines(response);
    } catch (Exception e) {
      System.err.println("Error fetching klines data: " + e.getMessage());
      return null;
    }
  }
}