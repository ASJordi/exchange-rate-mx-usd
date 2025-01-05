package dev.asjordi.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {

    public static void writeFile(String str, String content) {
        Path path = Paths.get(str);

        try (Writer w = new FileWriter(path.toFile());
            BufferedWriter bw = new BufferedWriter(w)) {
            bw.write(content);
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readAsSingleString(String str) {
        Path path = Paths.get(str);
        String lines;

        try {
            lines = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    public static List<String> readLines(String str) {
        Path path = Paths.get(str);
        List<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

}
