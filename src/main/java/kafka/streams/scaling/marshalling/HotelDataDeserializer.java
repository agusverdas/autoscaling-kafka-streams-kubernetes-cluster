package kafka.streams.scaling.marshalling;

import com.google.gson.Gson;
import kafka.streams.scaling.dto.HotelData;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HotelDataDeserializer implements Deserializer {
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  static private Gson gson = new Gson();

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    try {
      String hotel = new String(data, CHARSET);
      return gson.fromJson(hotel, HotelData.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error reading bytes!", e);
    }
  }

  @Override
  public void close() {

  }
}
