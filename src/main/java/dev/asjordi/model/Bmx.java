package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class representing the main BMX (Banco de México) data structure.
 * Contains a list of Series objects with exchange rate data.
 * Uses Lombok to generate boilerplate code and Jackson annotations for JSON processing.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bmx {

    /**
     * List of Series objects containing the exchange rate data.
     */
    private List<Series> series;

}
