package dev.asjordi.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class LoggerConfig {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private LoggerConfig() { }

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

    public static Logger getLogger() {
        return LOGGER;
    }

}
