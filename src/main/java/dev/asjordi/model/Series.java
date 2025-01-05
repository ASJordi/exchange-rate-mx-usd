package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Series {
    @JsonProperty("datos")
    private List<Dato> datos;

    @JsonProperty("idSerie")
    private String idSerie;

    @JsonProperty("titulo")
    private String titulo;

    public List<Dato> getDatos() {
        return datos;
    }

    public void setDatos(List<Dato> datos) {
        this.datos = datos;
    }

    public String getIdSerie() {
        return idSerie;
    }

    public void setIdSerie(String idSerie) {
        this.idSerie = idSerie;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        titulo = titulo.replaceAll("\\s+", " ");
        this.titulo = titulo;
    }


    @Override
    public String toString() {
        return "Series{" + "idSerie=" + idSerie +
                ", titulo='" + titulo + '\'' +
                ", datos='" + datos + '\'' +
                '}';
    }
}
