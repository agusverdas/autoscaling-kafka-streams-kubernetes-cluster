package kafka.streams.scaling.marshalling;

import com.google.gson.Gson;
import kafka.streams.scaling.dto.WeatherData;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.Charset;
import java.util.Map;

public class WeatherDataDeserializer implements Deserializer {
  private static final Charset CHARSET = Charset.forName("UTF-8");
  static private Gson gson = new Gson();

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    try {
      // Transform the bytes to String
      String weather = new String(data, CHARSET);
      // Return the Person object created from the String 'person'
      return gson.fromJson(weather, WeatherData.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error reading bytes! Yanlış", e);
    }
  }

  @Override
  public void close() {

  }
}
