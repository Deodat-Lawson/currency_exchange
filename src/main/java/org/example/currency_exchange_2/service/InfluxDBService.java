package org.example.currency_exchange_2.service;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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

  @PostConstruct
  public void PostConstruct() {
    if (applicationPropertiesMissing()) {
      System.err.println("InfluxDB application properties not defined");
      return;
    }
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
    writeApi = influxDBClient.getWriteApiBlocking();
    deleteApi = influxDBClient.getDeleteApi();
  }

  public boolean applicationPropertiesMissing() {
    return influxUrl == null || influxBucket == null || influxOrg == null || influxToken == null;
  }

  @Measurement(name = "Klines data")
  public static class KlinesData {
    @Column
    public String base;

    @Column
    public String quote;

    @Column
    public long openTime;

    @Column
    public long closeTime;

    @Column
    public int numberOfTrades;

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
  }

  public void writeKlinesData(KlinesData data) {
    if (writeApi == null) {
      throw new IllegalStateException("InfluxDB WriteApi not initialized");
    }
    writeApi.writeMeasurement(WritePrecision.NS, data);
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