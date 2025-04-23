package dev.asjordi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.asjordi.logger.LoggerConfig;
import dev.asjordi.model.Bmx;
import dev.asjordi.model.BmxResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles data mapping between different formats for the BMX exchange rate data.
 * This class is responsible for:
 * - Converting HTTP responses to Java objects
 * - Persisting Java objects to JSON files
 * - Reading JSON files and converting them back to Java objects
 * Uses Jackson for JSON serialization and deserialization.
 */
public class DataMapper {

    private static final Logger LOGGER = LoggerConfig.getLogger();
    private final ObjectMapper mapper;
    private final Path PATH = Path.of("data.json");

    /**
     * Constructor that initializes the Jackson ObjectMapper.
     * Configures the mapper to handle Java 8 date/time types properly.
     */
    public DataMapper() {
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LOGGER.log(Level.INFO, () -> "DataMapper initialized");
    }

    /**
     * Converts an HTTP response containing JSON data to a BmxResponse object.
     * 
     * @param response Optional HTTP response containing JSON data from the BMX API
     * @return BmxResponse object containing the parsed data
     * @throws RuntimeException if the response is empty, has a non-200 status code, or cannot be parsed
     */
    public BmxResponse mapDataToObject(Optional<HttpResponse<String>> response) {
        BmxResponse bmxResponse;
        LOGGER.log(Level.INFO, () -> "Mapping data to BmxResponse object");

        try {
            if (response.isPresent() && response.get().statusCode() == 200) {
                bmxResponse = mapper.readValue(response.get().body(), BmxResponse.class);
                LOGGER.log(Level.INFO, () -> "Data mapped to BmxResponse object successfully");
            } else {
                LOGGER.log(Level.SEVERE, () -> "Error getting data from API\n" + "Status code: " + response.get().statusCode());
                throw new RuntimeException("Error getting data from API");
            }
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Failed to map data to BmxResponse object", e);
            throw new RuntimeException(e.getMessage());
        }

        return bmxResponse;
    }

    /**
     * Persists a Bmx object to a JSON file.
     * 
     * @param bmx The Bmx object to be saved to file
     * @return true if the operation was successful, false otherwise
     */
    public boolean mapDataToFile(Bmx bmx) {
        LOGGER.log(Level.INFO, () -> "Mapping Bmx object to file");

        try {
            mapper.writeValue(PATH.toFile(), bmx);
            LOGGER.log(Level.INFO, () -> "Bmx object mapped to file successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to map Bmx object to file", e);
            return false;
        }

        return true;
    }

    /**
     * Reads a JSON file and converts it to a Bmx object.
     * 
     * @return Optional containing the Bmx object if the file exists and can be parsed,
     *         or an empty Optional otherwise
     */
    public Optional<Bmx> mapFileToObject() {
        Bmx bmx;
        LOGGER.log(Level.INFO, () -> "Mapping file to Bmx object");

        try {
            if (Files.exists(PATH) && Files.size(PATH) > 0) {
                bmx = mapper.readValue(PATH.toFile(), Bmx.class);
                LOGGER.log(Level.INFO, () -> "File mapped to Bmx object successfully");
            }
            else return Optional.empty();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to map file to Bmx object", e);
            return Optional.empty();
        }

        return Optional.ofNullable(bmx);
    }

}
