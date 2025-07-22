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

import java.net.BindException;

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
  public Klines fetchKlines(MarketData inputData) {
    String symbol = inputData.getBase() + inputData.getQuote();
    long startTime = inputData.getStartTime();
    long endTime = inputData.getEndTime();

    //TODO: If the endtime - starttime interval is greater than 500, output the entire list

    // Use String.format with the injected pattern
    String url = String.format(klinesUrlPattern, apiBaseUrl, symbol, startTime, endTime);
    Object[][] response = restTemplate.getForObject(url, Object[][].class);
    return new Klines(inputData.getExchangeId(), response);
  }

  @Recover
  public Klines handleError(Exception ex){
    throw new FetchDataRetryFailedException("Binance fetchedKlines failed after retries" + ex.getMessage());
  }

}