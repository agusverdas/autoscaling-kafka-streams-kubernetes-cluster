package kafka.streams.scaling.marshalling;

import kafka.streams.scaling.dto.JoinValue;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JoinValueSerde implements Serde<JoinValue> {
  private static JoinValueSerializer joinValueSerializer = new JoinValueSerializer();
  private static JoinValueDeserializer joinValueDeserializer = new JoinValueDeserializer();
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {

  }

  @Override
  public void close() {

  }

  @Override
  public Serializer<JoinValue> serializer() {
    return joinValueSerializer;
  }

  @Override
  public Deserializer<JoinValue> deserializer() {
    return joinValueDeserializer;
  }
}
