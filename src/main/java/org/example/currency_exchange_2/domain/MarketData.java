package org.example.currency_exchange_2.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@Validated
public class MarketData {
  @Min(0)
  private Integer exchangeId = 1;
  @NotBlank
  private String quote;
  @NotBlank
  private String base;

  private Long startTime = System.currentTimeMillis() - (60 * 60 * 1000L);
  private Long endTime = System.currentTimeMillis();

  //Mock data value to test controller
  public MarketData(Integer exchangeId, String quote, String base, Long startTime, Long endTime){
    this.exchangeId = exchangeId;
    this.quote  = quote;
    this.base = base;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}