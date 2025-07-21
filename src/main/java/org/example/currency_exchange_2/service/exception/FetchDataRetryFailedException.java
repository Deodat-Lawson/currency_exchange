package org.example.currency_exchange_2.service.exception;

public class FetchDataRetryFailedException extends RuntimeException {
  public FetchDataRetryFailedException(String message) {
    super(message);
  }
}
