package dev.asjordi;

import dev.asjordi.logger.LoggerConfig;
import java.util.logging.*;

/**
 * Main application class that serves as the entry point for the Exchange Rate mx-usd application.
 * This class initializes the logger and starts the BMX data processing.
 */
public class Main {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    /**
     * The main method that starts the application.
     * It sets up the logger and initiates the BMX data processing.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main( String[] args ) {
        LoggerConfig.setupLogger();
        LOGGER.log(Level.INFO, () -> "Starting BMX Data Processor");

        BmxDataProcessor bmxDataProcessor = new BmxDataProcessor();
        bmxDataProcessor.processData();
    }

}
