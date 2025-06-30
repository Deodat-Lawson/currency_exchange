package org.example.currency_exchange_2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Klines {
  long openTime;
  long closeTime;
  int numberOfTrades;
  String openPrice;
  String highPrice;
  String LowPrice;
  String closePrice;
  String Volume;
  String AssetVolume;
  String TakerBuyBaseAssetVolume;
  String TakerBuyQuoteAssetVolume;

  public Klines(Object[][] table) {
    if (table != null && table.length > 0 && table[0].length >= 12) {
      Object[] kline = table[0];
      this.openTime = Long.parseLong(kline[0].toString());
      this.openPrice = kline[1].toString();
      this.highPrice = kline[2].toString();
      this.LowPrice = kline[3].toString();
      this.closePrice = kline[4].toString();
      this.Volume = kline[5].toString();
      this.closeTime = Long.parseLong(kline[6].toString());
      this.AssetVolume = kline[7].toString();
      this.numberOfTrades = Integer.parseInt(kline[8].toString());
      this.TakerBuyBaseAssetVolume = kline[9].toString();
      this.TakerBuyQuoteAssetVolume = kline[10].toString();
    } else {
      this.openTime = -1;
      this.closeTime = -1;
      this.numberOfTrades = -1;
      this.openPrice = "-1";
      this.highPrice = "-1";
      this.LowPrice = "-1";
      this.closePrice = "-1";
      this.Volume = "-1";
      this.AssetVolume = "-1";
      this.TakerBuyBaseAssetVolume = "-1";
      this.TakerBuyQuoteAssetVolume = "-1";
    }
  }

}