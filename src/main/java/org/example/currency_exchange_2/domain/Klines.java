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

  public Klines(Integer exchangeId, Object[][] table) {
    if (table != null && table.length > 0) {
      Object[] kline = table[0];
      this.exchangeId = exchangeId;
      this.openTime = Long.parseLong(kline[0].toString());
      this.openPrice = kline[1].toString();
      this.highPrice = kline[2].toString();
      this.lowPrice = kline[3].toString();
      this.closePrice = kline[4].toString();
      this.volume = kline[5].toString();
      this.closeTime = Long.parseLong(kline[6].toString());
      this.assetVolume = kline[7].toString();
      this.numberOfTrades = Integer.parseInt(kline[8].toString());
      this.takerBuyBaseAssetVolume = kline[9].toString();
      this.takerBuyQuoteAssetVolume = kline[10].toString();
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