package yuca.profiler.geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import java.io.File;
import java.net.Socket;

public class GeoLocator {
  public static void getGeolocation() throws Exception {
    File database =
        new File("/home/timur/projects/sigprop/profiler/resources/GeoIP2-Country-Test.mmdb");
    DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

    Socket socket = new Socket("www.google.com", 80);
    CountryResponse response = dbReader.country(socket.getInetAddress());
    socket.close();

    System.out.println(response);

    String countryName = response.getCountry().getName();
    // String cityName = response.getCity().getName();
    // String postal = response.getPostal().getCode();
    // String state = response.getLeastSpecificSubdivision().getName();
  }

  public static void main(String[] args) throws Exception {
    getGeolocation();
  }
}
