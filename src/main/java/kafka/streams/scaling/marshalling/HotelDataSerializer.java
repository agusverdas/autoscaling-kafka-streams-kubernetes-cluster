package kafka.streams.scaling.marshalling;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.Charset;
import java.util.Map;

public class HotelDataSerializer implements Serializer {
  private static final Charset CHARSET = Charset.forName("UTF-8");
  static private Gson gson = new Gson();

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public byte[] serialize(String topic, Object data) {
    String line = gson.toJson(data);
    // Return the bytes from the String 'line'
    return line.getBytes(CHARSET);
  }

  @Override
  public void close() {

  }
}
