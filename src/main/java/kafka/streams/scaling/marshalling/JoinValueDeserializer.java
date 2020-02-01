package kafka.streams.scaling.marshalling;

import com.google.gson.Gson;
import kafka.streams.scaling.dto.JoinValue;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.Charset;
import java.util.Map;

public class JoinValueDeserializer implements Deserializer {
  private static final Charset CHARSET = Charset.forName("UTF-8");
  static private Gson gson = new Gson();

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    try {
      String weather = new String(data, CHARSET);
      return gson.fromJson(weather, JoinValue.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error reading bytes! Yanlış", e);
    }
  }

  @Override
  public void close() {

  }
}