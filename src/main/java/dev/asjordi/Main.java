package dev.asjordi;

import java.util.logging.*;

public class Main {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    public static void main( String[] args ) {
        LoggerConfig.setupLogger();
        LOGGER.log(Level.INFO, () -> "Starting BMX Data Processor");

        BmxDataProcessor bmxDataProcessor = new BmxDataProcessor();
        bmxDataProcessor.processData();
    }

}
