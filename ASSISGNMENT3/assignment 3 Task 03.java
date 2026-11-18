import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Demonstrates manual close() and try-with-resources for multi-file sales reporting. */
public class SalesReport {
    static void manualReport(List<Path> inputs, Path output) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(output);
            writer.write("DAILY SALES REPORT\n");
            for (Path input : inputs) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(input);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("ERROR")) throw new IOException("Bad record in " + input);
                        writer.write(line);
                        writer.newLine();
                    }
                } finally {
                    if (reader != null) reader.close();
                }
            }
        } finally {
            if (writer != null) writer.close();
        }
    }

    static void safeReport(List<Path> inputs, Path output) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("DAILY SALES REPORT");
            writer.newLine();
            for (Path input : inputs) {
                try (BufferedReader reader = Files.newBufferedReader(input)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("ERROR")) throw new IOException("Bad record in " + input);
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("sales-input");
        Files.createDirectories(dir);
        Path a = dir.resolve("store1.txt"), b = dir.resolve("store2.txt");
        Files.write(a, Arrays.asList("Chennai,1200", "Madurai,900"));
        Files.write(b, Arrays.asList("Coimbatore,1500", "ERROR_CORRUPT_RECORD"));
        List<Path> inputs = Arrays.asList(a,b);

        try { manualReport(inputs, dir.resolve("manual-report.txt")); }
        catch (IOException e) { System.out.println("Manual version failed: " + e.getMessage()); }
        try { safeReport(inputs, dir.resolve("safe-report.txt")); }
        catch (IOException e) { System.out.println("Try-with-resources version failed: " + e.getMessage()); }
        System.out.println("Both versions close opened files; inspect sales-input for partial reports.");
    }
}
