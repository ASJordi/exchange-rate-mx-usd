package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing the response from the BMX (Banco de México) API.
 * Serves as a wrapper for the Bmx object in the API response.
 * Uses Lombok to generate boilerplate code and Jackson annotations for JSON processing.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BmxResponse {

    /**
     * The Bmx object containing the exchange rate data.
     */
    private Bmx bmx;

}
