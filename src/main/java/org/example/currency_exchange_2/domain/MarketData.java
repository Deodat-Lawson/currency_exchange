package org.example.currency_exchange_2.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MarketData {
  int exchangeId;
  String quote;
  String base;
  long startTime;
  long endTime;
}