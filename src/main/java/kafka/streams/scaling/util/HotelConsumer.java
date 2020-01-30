package kafka.streams.scaling.util;

import kafka.streams.scaling.dto.HotelData;
import kafka.streams.scaling.marshalling.HotelDataDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public final class HotelConsumer {

  public static Consumer<String, HotelData> createConsumer() {
    final Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "sandbox-hdp.hortonworks.com:6667");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HotelDataDeserializer.class.getName());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    final Consumer<String, HotelData> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList("enriched_hotels"));
    consumer.seekToBeginning(consumer.assignment());
    return consumer;
  }

  public static Map<String, HotelData> consumeHotels(Consumer<String, HotelData> consumer){
    final int giveUp = 10;
    int noRecordsCount = 0;
    Map<String, HotelData> hotelData = new HashMap<>();
    consumer.poll(Duration.ofMillis(0));

    while (true) {
      final ConsumerRecords<String, HotelData> consumerRecords = consumer.poll(Duration.ofMillis(1000));
      if (consumerRecords.count() == 0) {
        noRecordsCount++;
        if (noRecordsCount > giveUp) break;
        else {
          continue;
        }
      }
      consumerRecords.forEach(record -> {
        hotelData.put(record.value().Geohash, record.value());
      });
      consumer.commitAsync();
    }
    consumer.close();
    System.out.println("Hotels data has been read");
    return hotelData;
  }
}
