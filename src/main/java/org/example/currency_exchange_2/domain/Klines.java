package org.example.currency_exchange_2.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Klines {
  Integer exchangeId;
  Long openTime;
  Long closeTime;
  Integer numberOfTrades;
  String openPrice;
  String highPrice;
  String lowPrice;
  String closePrice;
  String volume;
  String assetVolume;
  String takerBuyBaseAssetVolume;
  String takerBuyQuoteAssetVolume;

  public Klines(Integer exchangeId, Object[] table) {
    if (table != null && table.length > 0) {
      this.exchangeId = exchangeId;
      this.openTime = Long.parseLong(table[0].toString());
      this.openPrice = table[1].toString();
      this.highPrice = table[2].toString();
      this.lowPrice = table[3].toString();
      this.closePrice = table[4].toString();
      this.volume = table[5].toString();
      this.closeTime = Long.parseLong(table[6].toString());
      this.assetVolume = table[7].toString();
      this.numberOfTrades = Integer.parseInt(table[8].toString());
      this.takerBuyBaseAssetVolume = table[9].toString();
      this.takerBuyQuoteAssetVolume = table[10].toString();
    } else {
      this.exchangeId = -1;
      this.openTime = -1L;
      this.closeTime = -1L;
      this.numberOfTrades = -1;
      this.openPrice = "-1";
      this.highPrice = "-1";
      this.lowPrice = "-1";
      this.closePrice = "-1";
      this.volume = "-1";
      this.assetVolume = "-1";
      this.takerBuyBaseAssetVolume = "-1";
      this.takerBuyQuoteAssetVolume = "-1";
    }
  }
}