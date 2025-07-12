package org.example.currency_exchange_2;

import org.example.currency_exchange_2.controller.CurrencyMarketDataController;
import org.example.currency_exchange_2.domain.MarketData;
import org.example.currency_exchange_2.domain.exception.InputInvalidException;
import org.example.currency_exchange_2.service.BinanceDataService;
import org.example.currency_exchange_2.service.MarketDataValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class InputValidationTest {

  @Mock
  private MarketDataValidationService validationService;

  @Mock
  private BinanceDataService binanceDataService;

  @InjectMocks
  private CurrencyMarketDataController controller;

  @Test
  void testInvalidQuoteInputProperties() {
    MarketData invalidData = new MarketData();
    invalidData.setQuote("aksdjhfkashjdfk"); // Invalid currency code
    invalidData.setBase("USD");
    invalidData.setExchangeId(1);
    invalidData.setStartTime(1752308040668L);
    invalidData.setEndTime(1752308041668L);

    doThrow(new InputInvalidException("Invalid quote"))
            .when(validationService).checkData(any(MarketData.class));

    assertThrows(InputInvalidException.class, () -> {
      controller.postMarketData(invalidData);
    });
  }
}