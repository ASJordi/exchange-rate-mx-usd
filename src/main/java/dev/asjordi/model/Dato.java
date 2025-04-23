package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Model class representing a single data point in the BMX exchange rate data.
 * Contains the exchange rate value and the date it was recorded.
 * Uses Lombok to generate boilerplate code and Jackson annotations for JSON processing.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dato {
    /**
     * The exchange rate value as a string.
     */
    @JsonProperty("dato")
    private String dato;

    /**
     * The date when the exchange rate was recorded.
     * Formatted as "dd/MM/yyyy" in the JSON data.
     */
    @JsonProperty("fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fecha;

}
