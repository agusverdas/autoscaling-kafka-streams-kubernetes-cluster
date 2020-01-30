package kafka.streams.scaling.dto;

public class HotelData {
  public String Id;
  public String Name;
  public String Country;
  public String City;
  public String Address;
  public Double Latitude;
  public Double Longitude;
  public String Geohash;

  @Override
  public String toString() {
    return "HotelData{" +
            "Id='" + Id + '\'' +
            ", Name='" + Name + '\'' +
            ", Country='" + Country + '\'' +
            ", City='" + City + '\'' +
            ", Address='" + Address + '\'' +
            ", Latitude=" + Latitude +
            ", Longitude=" + Longitude +
            ", Geohash='" + Geohash + '\'' +
            '}';
  }
}
