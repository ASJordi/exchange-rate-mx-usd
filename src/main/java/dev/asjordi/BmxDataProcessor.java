package dev.asjordi;

import dev.asjordi.model.Bmx;
import dev.asjordi.model.Dato;
import dev.asjordi.util.FileUtils;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public class BmxDataProcessor {

    private final RequestManager requestManager;
    private final DataMapper dataMapper;

    public BmxDataProcessor() {
        this.requestManager = new RequestManager();
        this.dataMapper = new DataMapper();
    }

    public void processData() {
        var optionalCurrentData = dataMapper.mapFileToObject();
        var currentBmx = optionalCurrentData.orElse(new Bmx());

        var optionalResponseData = requestManager.makeRequest();
        var responseBmx = dataMapper.mapDataToObject(optionalResponseData);
        var newBmx = responseBmx.getBmx();

        newBmx.getSeries().forEach(newSerie -> {

            if (newSerie.getDatos() == null || newSerie.getDatos().isEmpty()) return;

            newSerie.getDatos().forEach(newDato -> {

                currentBmx.getSeries().stream()
                        .filter(s -> s.getIdSerie().equals(newSerie.getIdSerie()))
                        .findFirst()
                        .ifPresent(
                                s -> {
                                    var exists = s.getDatos()
                                            .stream()
                                            .anyMatch(d -> d.getFecha().equals(newDato.getFecha()));
                                    if (!exists) {
                                        s.getDatos().add(newDato);
                                        System.out.println("Added new data: " + newDato);
                                    }
                                }
                        );
            });
        });

        currentBmx.getSeries().forEach(serie -> serie.getDatos().sort(Comparator.comparing(Dato::getFecha)));

        AtomicReference<Dato> lastDato = new AtomicReference<>(null);

        currentBmx.getSeries().forEach(serie -> {
            var dato = serie.getDatos().stream().max(Comparator.comparing(Dato::getFecha));
            dato.ifPresent(lastDato::set);
        });

        if (lastDato.get() != null) {
            FileUtils.writeFile("lastUpdate.txt", lastDato.get().getFecha().toString());
            System.out.println("Last update: " + lastDato.get().getFecha());
        }

        var statusSave = dataMapper.mapDataToFile(currentBmx);

        if (statusSave) System.out.println("Data saved successfully");
        else System.out.println("An error occurred while saving the data");
    }
}
