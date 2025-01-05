package dev.asjordi;

import dev.asjordi.util.FileUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

public class RequestManager {

    private String API_URL = "https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF43718/datos/";
    private Properties props;
    private LocalDate today;
    private LocalDate lastFetch;

    public RequestManager() {
        this.today = LocalDate.now();
        getLastFetch();
        loadEnv();
    }

    private void getLastFetch() {
        var dateStr = FileUtils.readAsSingleString("lastUpdate.txt");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.lastFetch = LocalDate.parse(dateStr, formatter);
        this.API_URL = this.API_URL + this.lastFetch + "/" + this.today;
        System.out.println("API URL: " + this.API_URL);
    }

    private void loadEnv() {
        this.props = new Properties();
        this.props.setProperty("API_TOKEN_BMX", System.getenv("API_TOKEN_BMX"));
    }

    public Optional<HttpResponse<String>> makeRequest() {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.API_URL))
                    .version(HttpClient.Version.HTTP_2)
                    .header("Accept", "application/json")
                    .header("Bmx-Token", this.props.getProperty("API_TOKEN_BMX"))
                    .timeout(Duration.of(60, ChronoUnit.SECONDS))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.of(20, ChronoUnit.SECONDS))
                .build();

        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return Optional.ofNullable(response);
    }

}
