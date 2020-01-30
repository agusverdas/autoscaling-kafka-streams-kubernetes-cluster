package kafka.streams.scaling.dto;

public class JoinValue {
  public String Id;
  public String Name;
  public String Country;
  public String City;
  public String Address;
  public Double Latitude;
  public Double Longitude;
  public Double avg_tempr_f;
  public Double avg_tmpr_c;
  public String wthr_date;
  public String geohash;
  public Integer precision;

 public JoinValue(HotelData hotel, WeatherData weather, Integer precision) {
   this.Id = hotel.Id;
   this.Name = hotel.Name;
   this.Country = hotel.Country;
   this.City = hotel.City;
   this.Address = hotel.Address;
   this.Latitude = hotel.Latitude;
   this.Longitude = hotel.Longitude;
   this.avg_tempr_f = weather.avg_tempr_f;
   this.avg_tmpr_c = weather.avg_tmpr_c;
   this.wthr_date = weather.wthr_date;
   this.geohash = weather.geohash;
   this.precision = precision;
 }

  @Override
  public String toString() {
    return "JoinValue{" +
            "Id='" + Id + '\'' +
            ", Name='" + Name + '\'' +
            ", Country='" + Country + '\'' +
            ", City='" + City + '\'' +
            ", Address='" + Address + '\'' +
            ", Latitude=" + Latitude +
            ", Longitude=" + Longitude +
            ", avg_tempr_f=" + avg_tempr_f +
            ", avg_tmpr_c=" + avg_tmpr_c +
            ", wthr_date='" + wthr_date + '\'' +
            ", geohash='" + geohash + '\'' +
            ", precision=" + precision +
            '}';
  }
}
