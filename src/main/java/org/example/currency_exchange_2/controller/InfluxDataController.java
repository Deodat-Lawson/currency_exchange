package org.example.currency_exchange_2.controller;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/influx/v0")
public class InfluxDataController {

  @Value("${influx.url}")
  private String influxUrl;

  @Value("${influx.token}")
  private String influxToken;

  @Value("${influx.org}")
  private String influxOrg;

  @Value("${influx.bucket}")
  private String influxBucket;

  private WriteApiBlocking writeApi;

  @PostConstruct
  void PostConstruct (){
    if(applicationPropertiesCheck()){
      System.err.println("application properties not defined");
      return;
    }
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
    writeApi = influxDBClient.getWriteApiBlocking();
  }

  boolean applicationPropertiesCheck(){
    return influxUrl != null && influxBucket != null && influxOrg != null && influxToken != null;
  }

  @Measurement(name = "temperature")
  private static class Temperature {
    @Column(tag = true)
    String location;

    @Column
    Double value;

    @Column(timestamp = true)
    Instant time;
  }

  @PostMapping
  public String postMarketData() {
    try {
      Temperature temperature = new Temperature();
      temperature.location = "south";
      temperature.value = 62D;
      temperature.time = Instant.now();
      writeApi.writeMeasurement(WritePrecision.NS, temperature);
      return "finished writing Values";
    } catch (Exception e) {
      return "error: " + e.getMessage();
    }
  }
}