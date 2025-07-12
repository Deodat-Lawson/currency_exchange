package org.example.currency_exchange_2.service;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.domain.exception.InputInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.example.currency_exchange_2.domain.Klines;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.io.BufferedReader;

import java.util.List;

@Service
public class MarketDataValidationService {
  HashSet<String> symbolHashMap;
  private static final long MAX_TIME_RANGE_MS = 365L * 24 * 60 * 60 * 1000;

  @PostConstruct
  private void loadValidSymbols() throws IOException {
      ClassPathResource resource = new ClassPathResource("assets/binanceSymbol.csv");
      try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
        String line;
        while ((line = br.readLine()) != null) {
          symbolHashMap.add(line);
        }
      }
  }

  public void checkData(MarketData data){
    checkSymbol(data.getBase());
    checkSymbol(data.getQuote());
    checkQuoteNotEqualToBase(data.getQuote(), data.getBase());
    checkTime(data.getStartTime(), data.getEndTime());
  }

  private void checkSymbol(String symbol){
    if(!symbolHashMap.contains(symbol)){
      throw new InputInvalidException("Input Symbol is invalid");
    }
  }

  private void checkQuoteNotEqualToBase(String quote, String base){
    if(quote.equals(base)){
      throw new InputInvalidException("Input Quote cannot be the same as Base");
    }
  }

  private void checkTime(long startTime, long endTime){
    long currentTime = System.currentTimeMillis();

    if(startTime > currentTime){
      throw new InputInvalidException("StartTime cannot be in the future");
    }
    if(endTime > currentTime){
      throw new InputInvalidException("EndTime cannot be in the future");
    }
    if(endTime < startTime){
      throw new InputInvalidException("Input EndTime must be chronologically after StartTime");
    }
    if(endTime - startTime > MAX_TIME_RANGE_MS){
      throw new InputInvalidException("Time range cannot exceed " + (MAX_TIME_RANGE_MS / (24 * 60 * 60 * 1000)) + " days");
    }
  }
}