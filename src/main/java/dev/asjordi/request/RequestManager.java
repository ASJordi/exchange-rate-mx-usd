package dev.asjordi.request;

import dev.asjordi.logger.LoggerConfig;
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

/**
 * Manages HTTP requests to the Banxico API.
 */
public class RequestManager {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    private static final String BASE_API_URL = "https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF43718/datos/";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_LAST_FETCH_DATE = "2023-01-01";
    private static final String API_TOKEN_KEY = "API_TOKEN_BMX";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String BMX_TOKEN_HEADER = "Bmx-Token";
    private static final int REQUEST_TIMEOUT_SECONDS = 60;
    private static final int CONNECT_TIMEOUT_SECONDS = 20;

    private final String apiUrl;
    private final Properties properties;

    /**
     * Initializes a new RequestManager with the configured API URL and environment variables.
     */
    public RequestManager() {
        this.apiUrl = buildApiUrl();
        this.properties = loadEnvironmentVariables();
    }

    /**
     * Builds the complete API URL with date range.
     * 
     * @return The complete API URL
     */
    private String buildApiUrl() {
        LOGGER.log(Level.INFO, () -> "Building API URL");

        LocalDate lastFetch = getLastFetchDate();
        LocalDate today = LocalDate.now();

        String url = BASE_API_URL + lastFetch + "/" + today;
        LOGGER.log(Level.INFO, () -> "API URL: " + url);

        return url;
    }

    /**
     * Gets the last fetch date from the lastUpdate.txt file.
     * If the file cannot be read or parsed, returns a default date.
     * 
     * @return The last fetch date
     */
    private LocalDate getLastFetchDate() {
        LOGGER.log(Level.INFO, () -> "Attempting to read last fetch date");

        try {
            String dateStr = FileUtils.readAsSingleString("lastUpdate.txt");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            LocalDate lastFetch = LocalDate.parse(dateStr, formatter);
            LOGGER.log(Level.INFO, () -> "Last fetch date: " + lastFetch);
            return lastFetch;
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Failed to read last fetch date, using default date", e);
            return LocalDate.parse(DEFAULT_LAST_FETCH_DATE);
        }
    }

    /**
     * Loads environment variables needed for API requests.
     * 
     * @return Properties containing environment variables
     */
    private Properties loadEnvironmentVariables() {
        LOGGER.log(Level.INFO, () -> "Loading environment variables");

        Properties props = new Properties();
        String apiToken = System.getenv(API_TOKEN_KEY);

        if (apiToken == null || apiToken.isEmpty()) {
            LOGGER.log(Level.WARNING, () -> API_TOKEN_KEY + " environment variable is not set");
            apiToken = "";
        }

        props.setProperty(API_TOKEN_KEY, apiToken);
        LOGGER.log(Level.INFO, () -> "Environment variables loaded");

        return props;
    }

    /**
     * Creates an HTTP client with configured settings.
     * 
     * @return Configured HttpClient
     */
    private HttpClient createHttpClient() {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.of(CONNECT_TIMEOUT_SECONDS, ChronoUnit.SECONDS))
                .build();

        LOGGER.log(Level.INFO, () -> "HTTP client built successfully");
        return client;
    }

    /**
     * Creates an HTTP request with configured settings.
     * 
     * @return Optional containing the HTTP request if successful, empty otherwise
     */
    private Optional<HttpRequest> createHttpRequest() {
        LOGGER.log(Level.INFO, () -> "Building HTTP request");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.apiUrl))
                    .version(HttpClient.Version.HTTP_2)
                    .header(ACCEPT_HEADER, JSON_MIME_TYPE)
                    .header(BMX_TOKEN_HEADER, this.properties.getProperty(API_TOKEN_KEY))
                    .timeout(Duration.of(REQUEST_TIMEOUT_SECONDS, ChronoUnit.SECONDS))
                    .GET()
                    .build();

            LOGGER.log(Level.INFO, () -> "HTTP request built successfully");
            return Optional.of(request);
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Failed to build HTTP request", e);
            return Optional.empty();
        }
    }

    /**
     * Makes an HTTP request to the Banxico API.
     * 
     * @return Optional containing the HTTP response if successful, empty otherwise
     */
    public Optional<HttpResponse<String>> makeRequest() {
        LOGGER.log(Level.INFO, () -> "Starting HTTP request to BMX API");

        Optional<HttpRequest> requestOpt = createHttpRequest();
        if (requestOpt.isEmpty()) {
            return Optional.empty();
        }

        HttpClient client = createHttpClient();

        try {
            HttpResponse<String> response = client.send(requestOpt.get(), HttpResponse.BodyHandlers.ofString());
            LOGGER.log(Level.INFO, () -> "HTTP request sent successfully");
            return Optional.of(response);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send HTTP request", e);
            return Optional.empty();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "HTTP request interrupted", e);
            Thread.currentThread().interrupt(); // Restore interrupted status
            return Optional.empty();
        }
    }
}
