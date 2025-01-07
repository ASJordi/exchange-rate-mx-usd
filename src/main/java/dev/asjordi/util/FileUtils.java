package dev.asjordi.util;

import dev.asjordi.LoggerConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    private FileUtils() { }

    public static void writeFile(String str, String content) {
        Path path = Paths.get(str);

        LOGGER.log(Level.INFO, () -> "Attempting to write to file {}" + path);

        try (Writer w = new FileWriter(path.toFile());
            BufferedWriter bw = new BufferedWriter(w)) {
            bw.write(content);
            bw.flush();
            LOGGER.log(Level.INFO, () -> "Successfully wrote to file: " + path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write to file: " + path, e);
            throw new RuntimeException(e);
        }
    }

    public static String readAsSingleString(String str) {
        Path path = Paths.get(str);
        LOGGER.log(Level.INFO, () -> "Attempting to read file as a single string: " + path);
        String lines;

        try {
            lines = Files.readString(path);
            LOGGER.log(Level.INFO, () -> "Successfully read file as a single string: " + path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, () -> "Failed to read file as a single string: " + path);
            throw new RuntimeException(e);
        }

        return lines;
    }

    public static List<String> readLines(String str) {
        Path path = Paths.get(str);
        LOGGER.log(Level.INFO, () -> "Attempting to read file line by line: " + path);
        List<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            LOGGER.log(Level.INFO, () -> "Successfully read file line by line: " + path);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, () -> "Failed to read file line by line: " + path);
            throw new RuntimeException(e);
        }

        return lines;
    }

}
