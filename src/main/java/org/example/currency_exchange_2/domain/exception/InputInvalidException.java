package org.example.currency_exchange_2.domain.exception;

public class InputInvalidException extends RuntimeException{
  public InputInvalidException(String message) {
    super(message);
  }
}
