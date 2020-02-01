package kafka.streams.scaling.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.streams.scaling.dto.HotelData;
import kafka.streams.scaling.marshalling.HotelDataDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;

import java.time.Duration;
import java.util.*;

public final class HotelConsumer {

  public static Consumer<String, HotelData> createConsumer() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "sandbox-hdp.hortonworks.com:6667");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HotelDataDeserializer.class.getName());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    Consumer<String, HotelData> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList("enriched_hotels"));
    return consumer;
  }

  public static Map<String, List<HotelData>> consumeHotels(Consumer<String, HotelData> consumer){
    final int giveUp = 20;
    int noRecordsCount = 0;
    Map<String, List<HotelData>> hotelData = new HashMap<>();
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
        hotelData.computeIfAbsent(record.value().Geohash, k -> new ArrayList<>());
        hotelData.get(record.value().Geohash).add(record.value());
      });
      consumer.commitAsync();
    }
    consumer.close();
    System.out.println("Hotels data has been read");
    return hotelData;
  }
}
