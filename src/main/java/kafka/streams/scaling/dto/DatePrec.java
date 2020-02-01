package kafka.streams.scaling.dto;

public class DatePrec {
  public String date;
  public Integer prec;

  public DatePrec(String date, Integer prec) {
    this.date = date;
    this.prec = prec;
  }
}
