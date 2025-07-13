package org.example.currency_exchange_2.domain.exception;

public class DataNotFoundException extends RuntimeException {
  public DataNotFoundException(String message) {
    super(message);
  }
}
