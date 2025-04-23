package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class representing a series of exchange rate data points in the BMX API response.
 * Contains a list of data points along with metadata about the series.
 * Uses Lombok to generate boilerplate code and Jackson annotations for JSON processing.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series {
    /**
     * List of data points (exchange rate values with dates) in this series.
     */
    @JsonProperty("datos")
    private List<Dato> datos;

    /**
     * The unique identifier for this series in the BMX API.
     */
    @JsonProperty("idSerie")
    private String idSerie;

    /**
     * The descriptive title of this series.
     */
    @JsonProperty("titulo")
    private String titulo;

}
