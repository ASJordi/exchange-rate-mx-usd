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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestManager {

    private static final Logger LOGGER = LoggerConfig.getLogger();

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
        LOGGER.log(Level.INFO, () -> "Attempting to read last fetch date");

        var dateStr = FileUtils.readAsSingleString("lastUpdate.txt");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.lastFetch = LocalDate.parse(dateStr, formatter);
        this.API_URL = this.API_URL + this.lastFetch + "/" + this.today;

        LOGGER.log(Level.INFO, () -> "Last fetch date: " + this.lastFetch);
        LOGGER.log(Level.INFO, () -> "API URL: " + this.API_URL);
    }

    private void loadEnv() {
        LOGGER.log(Level.INFO, () -> "Attempting to load environment variables");
        this.props = new Properties();
        this.props.setProperty("API_TOKEN_BMX", System.getenv("API_TOKEN_BMX"));
        LOGGER.log(Level.INFO, () -> "Environment variables loaded");
    }

    public Optional<HttpResponse<String>> makeRequest() {
        LOGGER.log(Level.INFO, () -> "Starting HTTP request to BMX API");
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

            LOGGER.log(Level.INFO, () -> "HTTP request build successfully");
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Failed to build HTTP request", e);
        }

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.of(20, ChronoUnit.SECONDS))
                .build();

        LOGGER.log(Level.INFO, () -> "HTTP client build successfully");
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.log(Level.INFO, () -> "HTTP request sent successfully");
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to send HTTP request", e);
        }
        
        return Optional.ofNullable(response);
    }

}
