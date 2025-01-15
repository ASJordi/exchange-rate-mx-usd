package dev.asjordi;

import dev.asjordi.model.Bmx;
import dev.asjordi.model.Dato;
import dev.asjordi.util.FileUtils;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BmxDataProcessor {

    private static final Logger LOGGER = LoggerConfig.getLogger();
    private final RequestManager requestManager;
    private final DataMapper dataMapper;

    public BmxDataProcessor() {
        this.requestManager = new RequestManager();
        this.dataMapper = new DataMapper();
        LOGGER.log(Level.INFO, () -> "BmxDataProcessor initialized");
    }

    public void processData() {
        LOGGER.log(Level.INFO, () -> "Starting data processing");
        var optionalCurrentData = dataMapper.mapFileToObject();

        if (optionalCurrentData.isPresent()) {
            LOGGER.log(Level.INFO, () -> "Existing data found, updating data");
            updateData(optionalCurrentData);
        }
        else {
            LOGGER.log(Level.INFO, () -> "No existing data found, creating initial data");
            createInitialData();
        }
    }

    private void createInitialData() {
        LOGGER.log(Level.INFO, () -> "Creating initial data");

        var optionalResponseData = requestManager.makeRequest();
        var responseBmx = dataMapper.mapDataToObject(optionalResponseData);
        var newBmx = responseBmx.getBmx();

        newBmx.getSeries().removeIf(serie -> serie.getDatos() == null || serie.getDatos().isEmpty());

        newBmx.getSeries().forEach(serie -> serie.getDatos().sort(Comparator.comparing(Dato::getFecha)));

        AtomicReference<Dato> lastDato = new AtomicReference<>(null);

        newBmx.getSeries().forEach(serie -> {
            var dato = serie.getDatos().stream().max(Comparator.comparing(Dato::getFecha));
            dato.ifPresent(lastDato::set);
        });

        if (lastDato.get() != null) {
            FileUtils.writeFile("lastUpdate.txt", lastDato.get().getFecha().toString());
            LOGGER.log(Level.INFO, () -> "Last update: " + lastDato.get().getFecha());
        }

        var statusSave = dataMapper.mapDataToFile(newBmx);

        if (statusSave) LOGGER.log(Level.INFO, () -> "Data saved successfully");
        else LOGGER.log(Level.SEVERE, () -> "An error occurred while saving the data");

        generateChart(newBmx);
    }

    private void updateData(Optional<Bmx> optionalCurrentData) {
        LOGGER.log(Level.INFO, () -> "Updating data");
        var currentBmx = optionalCurrentData.orElse(new Bmx());

        var optionalResponseData = requestManager.makeRequest();
        var responseBmx = dataMapper.mapDataToObject(optionalResponseData);
        var newBmx = responseBmx.getBmx();

        newBmx.getSeries().forEach(newSerie -> {

            if (newSerie.getDatos() == null || newSerie.getDatos().isEmpty()) return;

            newSerie.getDatos().forEach(newDato -> currentBmx.getSeries().stream()
                    .filter(s -> s.getIdSerie().equals(newSerie.getIdSerie()))
                    .findFirst()
                    .ifPresent(
                            s -> {
                                var exists = s.getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getFecha().equals(newDato.getFecha()));
                                if (!exists) {
                                    s.getDatos().add(newDato);
                                    LOGGER.log(Level.INFO, () -> "Added new data: " + newDato);
                                }
                            }
                    ));
        });

        currentBmx.getSeries().forEach(serie -> serie.getDatos().sort(Comparator.comparing(Dato::getFecha)));

        AtomicReference<Dato> lastDato = new AtomicReference<>(null);

        currentBmx.getSeries().forEach(serie -> {
            var dato = serie.getDatos().stream().max(Comparator.comparing(Dato::getFecha));
            dato.ifPresent(lastDato::set);
        });

        if (lastDato.get() != null) {
            FileUtils.writeFile("lastUpdate.txt", lastDato.get().getFecha().toString());
            LOGGER.log(Level.INFO, () -> "Last update: " + lastDato.get().getFecha());
        }

        var statusSave = dataMapper.mapDataToFile(currentBmx);

        if (statusSave) LOGGER.log(Level.INFO, () -> "Data saved successfully");
        else LOGGER.log(Level.SEVERE, () -> "An error occurred while saving the data");

        generateChart(currentBmx);
    }

    private void generateChart(Bmx bmx) {
        TimeSeriesChart seriesChart = new TimeSeriesChart(bmx);
        seriesChart.generateChart();
    }

}
