package org.example.currency_exchange_2.service;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class InfluxDBService {

  @Value("${influx.url}")
  private String influxUrl;

  @Value("${influx.token}")
  private String influxToken;

  @Value("${influx.org}")
  private String influxOrg;

  @Value("${influx.bucket}")
  private String influxBucket;

  private WriteApiBlocking writeApi;
  private DeleteApi deleteApi;
  private QueryApi queryApi;

  @PostConstruct
  public void PostConstruct() {
    if (applicationPropertiesMissing()) {
      System.err.println("InfluxDB application properties not defined");
      return;
    }
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
    writeApi = influxDBClient.getWriteApiBlocking();
    deleteApi = influxDBClient.getDeleteApi();
    queryApi = influxDBClient.getQueryApi();
  }

  public boolean applicationPropertiesMissing() {
    return influxUrl == null || influxBucket == null || influxOrg == null || influxToken == null;
  }

  @Measurement(name = "Klines data")
  public static class KlinesData {
    @Column(timestamp = true)
    public Instant time;

    @Column(tag = true)
    public String exchangeId;

    @Column(tag = true)
    public String base;

    @Column(tag = true)
    public String quote;

    @Column
    public Long openTime;

    @Column
    public Long closeTime;

    @Column
    public Integer numberOfTrades;

    @Column
    public String openPrice;

    @Column
    public String closePrice;

    @Column
    public String highPrice;

    @Column
    public String lowPrice;

    @Column
    public String volume;

    @Column
    public String assetVolume;

    @Column
    public String takerBuyBaseAssetVolume;

    @Column
    public String takerBuyQuoteAssetVolume;

    // Default constructor
    public KlinesData() {
      this.time = Instant.now();
    }
  }

  public void writeKlinesData(KlinesData data) {
    if (writeApi == null) {
      throw new IllegalStateException("InfluxDB WriteApi not initialized");
    }
    writeApi.writeMeasurement(WritePrecision.NS, data);
  }

  public List<KlinesData> queryKlinesData(Integer exchangeId){
    //InfluxDb stores tags as String, so we need to parse in the exchange Id as a String
    String flux = String.format(
            "from(bucket: \"%s\") " +
                    "|> range(start: -30d) " +
                    "|> filter(fn: (r) => r[\"_measurement\"] == \"Klines data\") " +
                    "|> filter(fn: (r) => r[\"exchangeId\"] == \"%s\") " +
                    "|> group(columns: [\"base\", \"quote\", \"exchangeId\", \"_field\"]) " +
                    "|> last() " +
                    "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
            influxBucket, exchangeId.toString()
    );
    return queryApi.query(flux, KlinesData.class);
  }

  public void deleteBySymbol(String symbol) {
    if (deleteApi == null) {
      throw new IllegalStateException("InfluxDB DeleteApi not initialized");
    }
    try {
      String predicate = String.format("symbol=\"%s\"", symbol);
      deleteApi.delete(
              OffsetDateTime.parse("1970-01-01T00:00:00Z"),
              OffsetDateTime.parse("2030-01-01T00:00:00Z"),
              predicate,
              influxBucket,
              influxOrg
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete symbol data: " + symbol, e);
    }
  }

  public void clearAllData() {
    if (deleteApi == null) {
      throw new IllegalStateException("InfluxDB DeleteApi not initialized");
    }
    try {
      deleteApi.delete(
              OffsetDateTime.parse("1970-01-01T00:00:00Z"),
              OffsetDateTime.parse("2030-01-01T00:00:00Z"),
              "",
              influxBucket,
              influxOrg
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to clear all data", e);
    }
  }
}