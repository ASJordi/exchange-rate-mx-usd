package dev.asjordi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BmxResponse {
    private Bmx bmx;

    public Bmx getBmx() {
        return bmx;
    }

    public void setBmx(Bmx bmx) {
        this.bmx = bmx;
    }
}