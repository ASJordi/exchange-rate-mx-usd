package dev.asjordi.util;

import dev.asjordi.logger.LoggerConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing file operations for the application.
 * Contains methods for reading from and writing to files.
 * All methods are static and the class cannot be instantiated.
 */
public class FileUtils {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileUtils() { }

    /**
     * Writes content to a file at the specified path.
     * Creates the file if it doesn't exist, or overwrites it if it does.
     * 
     * @param str The path to the file
     * @param content The content to write to the file
     * @throws RuntimeException if an I/O error occurs
     */
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

    /**
     * Reads the entire contents of a file as a single string.
     * 
     * @param str The path to the file
     * @return The contents of the file as a string
     * @throws RuntimeException if the file cannot be read
     */
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

    /**
     * Reads a file line by line and returns the lines as a list of strings.
     * 
     * @param str The path to the file
     * @return A list containing each line of the file as a separate string
     * @throws RuntimeException if the file cannot be read
     */
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
