package kafka.streams.scaling.marshalling;

import kafka.streams.scaling.dto.HotelData;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class HotelDataSerde implements Serde<HotelData> {
  private static HotelDataSerializer hotelDataSerializer = new HotelDataSerializer();
  private static HotelDataDeserializer hotelDataDeserializer = new HotelDataDeserializer();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {

  }

  @Override
  public void close() {

  }

  @Override
  public Serializer<HotelData> serializer() {
    return hotelDataSerializer;
  }

  @Override
  public Deserializer<HotelData> deserializer() {
    return hotelDataDeserializer;
  }
}
