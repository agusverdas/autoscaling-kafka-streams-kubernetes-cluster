package kafka.streams.scaling.marshalling;

import kafka.streams.scaling.dto.WeatherData;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class WeatherDataSerde implements Serde<WeatherData> {
  private static WeatherDataSerializer weatherDataSerializer = new WeatherDataSerializer();
  private static WeatherDataDeserializer weatherDataDeserializer = new WeatherDataDeserializer();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    weatherDataSerializer.close();
    weatherDataDeserializer.close();
  }

  @Override
  public void close() {

  }

  @Override
  public Serializer<WeatherData> serializer() {
    return weatherDataSerializer;
  }

  @Override
  public Deserializer<WeatherData> deserializer() {
    return weatherDataDeserializer;
  }
}
