package dev.asjordi;

import dev.asjordi.chart.TimeSeriesChart;
import dev.asjordi.logger.LoggerConfig;
import dev.asjordi.model.Bmx;
import dev.asjordi.model.Dato;
import dev.asjordi.request.RequestManager;
import dev.asjordi.util.FileUtils;
import java.util.Comparator;
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
            updateData(optionalCurrentData.get());
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

        saveLastUpdateToFile(newBmx);
        saveDataToFile(newBmx);
        generateChart(newBmx);
    }

    private void updateData(Bmx currentBmx) {
        LOGGER.log(Level.INFO, () -> "Updating data");

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

        saveLastUpdateToFile(currentBmx);
        saveDataToFile(currentBmx);
        generateChart(currentBmx);
    }

    private void saveDataToFile(Bmx bmx) {
        var statusSave = dataMapper.mapDataToFile(bmx);
        if (statusSave) LOGGER.log(Level.INFO, () -> "Data saved successfully");
        else LOGGER.log(Level.SEVERE, () -> "An error occurred while saving the data");
    }

    private static void saveLastUpdateToFile(Bmx bmx) {
        AtomicReference<Dato> lastDato = new AtomicReference<>(null);

        bmx.getSeries().forEach(serie -> {
            var dato = serie.getDatos().stream().max(Comparator.comparing(Dato::getFecha));
            dato.ifPresent(lastDato::set);
        });

        if (lastDato.get() != null) {
            FileUtils.writeFile("lastUpdate.txt", lastDato.get().getFecha().toString());
            LOGGER.log(Level.INFO, () -> "Last update: " + lastDato.get().getFecha());
        }
    }

    private void generateChart(Bmx bmx) {
        TimeSeriesChart seriesChart = new TimeSeriesChart(bmx);
        seriesChart.generateChart();
    }

}
