package org.example.currency_exchange_2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class MarketData {
  int exchangeId;
  String quote;
  String base;
  long startTime;
  long endTime;
}