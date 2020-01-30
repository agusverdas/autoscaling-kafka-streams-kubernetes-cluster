package kafka.streams.scaling;

import java.util.*;

import ch.hsr.geohash.GeoHash;
import kafka.streams.scaling.dto.HotelData;
import kafka.streams.scaling.dto.JoinValue;
import kafka.streams.scaling.dto.WeatherData;
import kafka.streams.scaling.marshalling.WeatherDataSerde;
import kafka.streams.scaling.util.GeohashMapCreator;
import kafka.streams.scaling.util.HotelConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.log4j.Logger;


public class App {

  static final String DONE = "done";
  private static final Logger log = Logger.getLogger(App.class);

  public static void main(String[] args) {
    Consumer<String, HotelData> consumer = HotelConsumer.createConsumer();
    Map<String, HotelData> geohash5ToHotelMap = HotelConsumer.consumeHotels(consumer);
    Map<String, HotelData> geohash4ToHotelMap = GeohashMapCreator.geohashMap(geohash5ToHotelMap, 4);
    Map<String, HotelData> geohash3ToHotelMap = GeohashMapCreator.geohashMap(geohash5ToHotelMap, 3);

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "ks-scaling-id");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
      Optional.ofNullable(System.getenv("BOOTSTRAP_SERVERS_CONFIG")).orElse("sandbox-hdp.hortonworks.com:6667")
    );
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    StreamsBuilder builder = new StreamsBuilder();
    Printed<String, JoinValue> out = Printed.toSysOut();

    KStream<String, WeatherData> weatherDataKStream = builder.stream("hive_weather_data", Consumed.with(Serdes.String(), new WeatherDataSerde()));
    KStream<String, WeatherData> enrichedWeatherStream = weatherDataKStream
            .peek((k, v) -> v.geohash = GeoHash.withCharacterPrecision(v.lat, v.lng, 5).toBase32());
    enrichedWeatherStream.filter(((key, value) -> geohash5ToHotelMap.containsKey(value.geohash)
            || geohash4ToHotelMap.containsKey(value.geohash.substring(0, 5))
            || geohash3ToHotelMap.containsKey(value.geohash.substring(0, 4))))
            .mapValues(data -> {
              if (geohash5ToHotelMap.containsKey(data.geohash)) {
                return new JoinValue(geohash5ToHotelMap.get(data.geohash), data, 5);
              } else if (geohash4ToHotelMap.containsKey(data.geohash)) {
                return new JoinValue(geohash4ToHotelMap.get(data.geohash.substring(0, 4)), data, 4);
              } else {
                return new JoinValue(geohash3ToHotelMap.get(data.geohash.substring(0, 3)), data, 3);
              }
    }).print(out);
    KafkaStreams streams = new KafkaStreams(builder.build(), config);
    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        streams.close();
        log.info("Stream stopped");
      } catch (Exception exc) {
        log.error("Got exception while executing shutdown hook: ", exc);
      }
    }));
  }
}

