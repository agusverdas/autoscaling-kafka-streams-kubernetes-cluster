package kafka.streams.scaling.util;

import kafka.streams.scaling.dto.HotelData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeohashMapCreator {
  public static Map<String, List<HotelData>> geohashMap(Map<String, List<HotelData>> geohash5HotelMap, int prec) {
    Map<String, List<HotelData>> geohashMap = new HashMap<>();
    geohash5HotelMap.forEach((key, value) -> {
      geohashMap.put(key.substring(0, prec), value);
    });
    return geohashMap;
  }
}
