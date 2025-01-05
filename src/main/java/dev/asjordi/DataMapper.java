package dev.asjordi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.asjordi.model.Bmx;
import dev.asjordi.model.BmxResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class DataMapper {

    private ObjectMapper mapper;
    private final Path PATH = Path.of("data.json");

    public DataMapper() {
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public BmxResponse mapDataToObject(Optional<HttpResponse<String>> response) {
        BmxResponse bmxResponse;

        try {
            if (response.isPresent() && response.get().statusCode() == 200) {
                bmxResponse = mapper.readValue(response.get().body(), BmxResponse.class);
            } else {
                throw new RuntimeException("Error getting data from Banxico API\n" + "Status code: " + response.get().statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return bmxResponse;
    }

    public boolean mapDataToFile(Bmx bmx) {
        try {
            mapper.writeValue(PATH.toFile(), bmx);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Optional<Bmx> mapFileToObject() {
        Bmx bmx;

        try {
            if (Files.exists(PATH) && Files.size(PATH) > 0) bmx = mapper.readValue(PATH.toFile(), Bmx.class);
            else return Optional.empty();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.ofNullable(bmx);
    }

}
