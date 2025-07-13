package org.example.currency_exchange_2.controller.exception;

import org.example.currency_exchange_2.domain.exception.DataNotFoundException;
import org.example.currency_exchange_2.domain.exception.InputInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CurrencyMarketDataControllerExceptionHandler {
  @ExceptionHandler({InputInvalidException.class, DataNotFoundException.class})
  public ResponseEntity<String> handle(InputInvalidException ex){
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
