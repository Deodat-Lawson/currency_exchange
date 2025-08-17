package org.example.currency_exchange_2.service;

import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.service.exception.FetchDataRetryFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.example.currency_exchange_2.domain.Klines;
import org.springframework.retry.annotation.EnableRetry;

import java.util.ArrayList;

@Service
@EnableRetry
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

  @Retryable(
          retryFor = {RestClientException.class, RuntimeException.class},
          maxAttempts = 3,
          backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  public ArrayList<Klines> fetchKlines(MarketData inputData) {
    String symbol = inputData.getBase() + inputData.getQuote();
    long startTime = inputData.getStartTime();
    long endTime = inputData.getEndTime();

    ArrayList<Klines> klinesList = new ArrayList<>();

    long currentStart = startTime;
    System.out.println("fetching klines");

    while(endTime > currentStart){
      long currentEnd = Math.min(endTime, currentStart + 5000);
      String url = String.format(klinesUrlPattern, apiBaseUrl, symbol, currentStart, currentEnd);
      Object[][] response = restTemplate.getForObject(url, Object[][].class);
      System.out.println("response" + response.length + " " + response[0].length);
      Klines currentInterval = new Klines(inputData.getExchangeId(), response);
      System.out.println("adding klines" + currentInterval);
      klinesList.add(currentInterval);
      currentStart += 5000;
    }

    return klinesList;
  }

  @Recover
  public Klines handleError(Exception ex){
    throw new FetchDataRetryFailedException("Binance fetchedKlines failed after retries" + ex.getMessage());
  }

}