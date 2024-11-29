// OLD
public class WeatherService {
    public void getWeatherFromAPI() {
        System.out.println("Fetching weather from API...");
    }
}
public class WeatherApp {
    private WeatherService service = new WeatherService();
    public void showWeather() {
        service.getWeatherFromAPI() ;
    }
}

// NEW
public interface WeatherDataSource {
    void getWeatherFromAPI();
}

public class WeatherService {
    private final WeatherDataSource dataSource;

    public WeatherService(WeatherDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getWeatherFromAPI() {
        dataSource.getWeatherFromAPI();
    }
}

