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

/**
 * Processor for Banco de México (BMX) exchange rate data.
 * This class is responsible for fetching, processing, updating, and visualizing
 * exchange rate data from the BMX API. It handles both initial data creation
 * and updates to existing data.
 */
public class BmxDataProcessor {

    private static final Logger LOGGER = LoggerConfig.getLogger();
    private final RequestManager requestManager;
    private final DataMapper dataMapper;

    /**
     * Constructor that initializes the RequestManager and DataMapper.
     * Sets up the necessary components for processing BMX data.
     */
    public BmxDataProcessor() {
        this.requestManager = new RequestManager();
        this.dataMapper = new DataMapper();
        LOGGER.log(Level.INFO, () -> "BmxDataProcessor initialized");
    }

    /**
     * Main method to process BMX data.
     * Checks if existing data is available and either updates it or creates initial data.
     */
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

    /**
     * Creates initial data when no existing data is found.
     * Fetches data from the BMX API, processes it, saves it to files, and generates a chart.
     */
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

    /**
     * Updates existing data with new data from the BMX API.
     * Adds new data points to the existing data, sorts them by date,
     * saves the updated data to files, and regenerates the chart.
     * 
     * @param currentBmx The existing BMX data to be updated
     */
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

    /**
     * Saves the BMX data to a file using the DataMapper.
     * Logs the status of the save operation.
     * 
     * @param bmx The BMX data to be saved
     */
    private void saveDataToFile(Bmx bmx) {
        var statusSave = dataMapper.mapDataToFile(bmx);
        if (statusSave) LOGGER.log(Level.INFO, () -> "Data saved successfully");
        else LOGGER.log(Level.SEVERE, () -> "An error occurred while saving the data");
    }

    /**
     * Saves the date of the most recent data point to a file.
     * Finds the most recent date across all series and writes it to lastUpdate.txt.
     * 
     * @param bmx The BMX data containing the series with dates
     */
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

    /**
     * Generates a time series chart from the BMX data.
     * Creates a new TimeSeriesChart instance and calls its generateChart method.
     * 
     * @param bmx The BMX data to be visualized in the chart
     */
    private void generateChart(Bmx bmx) {
        TimeSeriesChart seriesChart = new TimeSeriesChart(bmx);
        seriesChart.generateChart();
    }

}
