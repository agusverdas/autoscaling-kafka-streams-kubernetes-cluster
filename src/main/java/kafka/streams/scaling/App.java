package kafka.streams.scaling;

import java.util.*;
import java.util.stream.Collectors;

import ch.hsr.geohash.GeoHash;
import kafka.streams.scaling.dto.DatePrec;
import kafka.streams.scaling.dto.HotelData;
import kafka.streams.scaling.dto.JoinValue;
import kafka.streams.scaling.dto.WeatherData;
import kafka.streams.scaling.marshalling.JoinValueSerde;
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
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;

import static java.util.stream.Collectors.*;


public class App {

  private static final Logger log = Logger.getLogger(App.class);

  public static void main(String[] args) throws InterruptedException {
    Consumer<String, HotelData> consumer = HotelConsumer.createConsumer();
    Map<String, List<HotelData>> hotelDataMap5 = HotelConsumer.consumeHotels(consumer);
    Map<String, List<HotelData>> hotelDataMap4 = GeohashMapCreator.geohashMap(hotelDataMap5, 4);
    Map<String, List<HotelData>> hotelDataMap3 = GeohashMapCreator.geohashMap(hotelDataMap5, 3);

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "weather-scaling-data");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
      Optional.ofNullable(System.getenv("BOOTSTRAP_SERVERS_CONFIG")).orElse("sandbox-hdp.hortonworks.com:6667")
    );
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    StreamsBuilder builder = new StreamsBuilder();
    Map<String, List<String>> map = new HashMap<>();
    KStream<String, WeatherData> weatherDataKStream = builder.stream("hive_weather", Consumed.with(Serdes.String(), new WeatherDataSerde()));
    weatherDataKStream.mapValues(value -> {
      value.geohash = GeoHash.geoHashStringWithCharacterPrecision(value.lat, value.lng, 5);
      return value;
    }).filter((key, value) -> hotelDataMap5.containsKey(value.geohash)
            || hotelDataMap4.containsKey(value.geohash.substring(0, 4))
            || hotelDataMap3.containsKey(value.geohash.substring(0, 3)))
            .flatMapValues(value -> {
              if (hotelDataMap5.containsKey(value.geohash)) {
                List<HotelData> hotels = hotelDataMap5.get(value.geohash);
                hotels.forEach(h -> {
                  map.computeIfAbsent(h.Id, k -> new ArrayList<>());
                });
                return hotels.stream()
                        .filter(h -> !map.get(h.Id).contains(value.wthr_date))
                        .peek(h -> map.get(h.Id).add(value.wthr_date))
                        .map(hotel -> new JoinValue(hotel, value, 5))
                        .collect(toList());
              }
              else if (hotelDataMap4.containsKey(value.geohash.substring(0, 4))) {
                List<HotelData> hotels = hotelDataMap4.get(value.geohash.substring(0, 4));
                hotels.forEach(h -> {
                  map.computeIfAbsent(h.Id, k -> new ArrayList<>());
                });
                return hotels.stream()
                        .filter(h -> !map.get(h.Id).contains(value.wthr_date))
                        .peek(h -> map.get(h.Id).add(value.wthr_date))
                        .map(hotel -> new JoinValue(hotel, value, 4))
                        .collect(toList());
              }
              else {
                List<HotelData> hotels = hotelDataMap3.get(value.geohash.substring(0, 3));
                hotels.forEach(h -> {
                  map.computeIfAbsent(h.Id, k -> new ArrayList<>());
                });
                return hotels.stream()
                        .filter(h -> !(map.get(h.Id).contains(value.wthr_date)))
                        .peek(h -> map.get(h.Id).add(value.wthr_date))
                        .map(hotel -> new JoinValue(hotel, value, 3))
                        .collect(toList());
              }
            }).to("day_weather_hotel", Produced.with(Serdes.String(), new JoinValueSerde()));

    KafkaStreams streams = new KafkaStreams(builder.build(), config);
    streams.start();

    Thread.sleep(3_600_000L);

    streams.close();
  }
}

