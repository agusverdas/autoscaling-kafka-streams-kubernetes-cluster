package kafka.streams.scaling.util;

import kafka.streams.scaling.dto.HotelData;

import java.util.HashMap;
import java.util.Map;

public class GeohashMapCreator {
  public static Map<String, HotelData> geohashMap(Map<String, HotelData> geohash5HotelMap, int prec) {
    Map<String, HotelData> geohashMap = new HashMap<>();
    geohash5HotelMap.forEach((key, value) -> {
      geohashMap.put(key.substring(0, prec), value);
    });
    return geohashMap;
  }
}
