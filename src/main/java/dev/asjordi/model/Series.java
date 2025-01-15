package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series {
    @JsonProperty("datos")
    private List<Dato> datos;

    @JsonProperty("idSerie")
    private String idSerie;

    @JsonProperty("titulo")
    private String titulo;

}
