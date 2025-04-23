package dev.asjordi.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

/**
 * Configuration class for the application's logging system.
 * Provides methods to set up and access a global logger instance.
 * The logger is configured to write to rotating log files in XML format.
 */
public class LoggerConfig {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LoggerConfig() { }

    /**
     * Sets up the global logger with file handlers.
     * Creates a logs directory if it doesn't exist and configures the logger
     * to write to rotating log files with XML formatting.
     * Each log file is limited to 1MB, with a maximum of 10 files.
     */
    public static void setupLogger() {
        try {
            Files.createDirectories(Paths.get("logs"));
            int limit = 1024 * 1024;
            int fileCount = 10;
            FileHandler fn = new FileHandler("logs/app.%g.log", limit, fileCount, true);
            fn.setFormatter(new XMLFormatter());
            LOGGER.addHandler(fn);
            LOGGER.setLevel(Level.ALL);
            LOGGER.setUseParentHandlers(true);
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }

    /**
     * Provides access to the global logger instance.
     * 
     * @return The configured Logger instance for the application
     */
    public static Logger getLogger() {
        return LOGGER;
    }

}
