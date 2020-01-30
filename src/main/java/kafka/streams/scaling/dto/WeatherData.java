package kafka.streams.scaling.dto;

public class WeatherData {
  public Double lng;
  public Double lat;
  public Double avg_tempr_f;
  public Double avg_tmpr_c;
  public String wthr_date;
  public String geohash;

  @Override
  public String toString() {
    return "WeatherData{" +
            "lng=" + lng +
            ", lat=" + lat +
            ", avg_tempr_f=" + avg_tempr_f +
            ", avg_tmpr_c=" + avg_tmpr_c +
            ", wthr_date='" + wthr_date + '\'' +
            ", geohash='" + geohash + '\'' +
            '}';
  }
}
