package org.example.currency_exchange_2.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

//TODO: Add in reasonable initial values for all the variables if its not provided in the parameters

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class MarketData {
  @Min(0)
  private Integer exchangeId;
  @NotBlank
  private String quote;
  @NotBlank
  private String base;

  private Long startTime;
  private Long endTime;
}