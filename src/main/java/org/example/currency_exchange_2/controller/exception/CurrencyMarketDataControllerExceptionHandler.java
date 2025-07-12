package org.example.currency_exchange_2.controller.exception;

import org.example.currency_exchange_2.domain.exception.InputInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.*;
import java.util.*;

@ControllerAdvice
public class CurrencyMarketDataControllerExceptionHandler {
  @ExceptionHandler({InputInvalidException.class})
  public ResponseEntity<String> handle(InputInvalidException ex){
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
