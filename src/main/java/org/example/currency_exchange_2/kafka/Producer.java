package org.example.currency_exchange_2.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {
  private static final Logger log = LoggerFactory.getLogger(Producer.class);

  public static void main(String[] args) {
    log.info("Kafka Producer");

    String bootstrapServers = "127.0.0.1:9092";

    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    //create the producer
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    for (int i=0; i<10; i++ ) {

      String topic = "demo";
      String value = "hello world " + Integer.toString(i);
      String key = "id_" + Integer.toString(i);

      ProducerRecord<String, String> producerRecord =
              new ProducerRecord<>(topic, key, value);

      producer.send(producerRecord, new Callback() {
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
          if (e == null) {
            log.info("Received new metadata. \n" +
                    "Topic:" + recordMetadata.topic() + "\n" +
                    "Key:" + producerRecord.key() + "\n" +
                    "Partition: " + recordMetadata.partition() + "\n" +
                    "Offset: " + recordMetadata.offset() + "\n" +
                    "Timestamp: " + recordMetadata.timestamp());
          } else {
            log.error("Error while producing", e);
          }
        }
      });
    }

    producer.flush();
    producer.close();


  }
}