package utility;

import Model.ChangeRecord;
import Model.FilePair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Responsible for reading data from CSV files.
 */
public class CSVImporter {
    /**
     * Reads co-changing file pairs from the given CSV file.
     * @param csvFile File containing co-changes.
     */
    public static ArrayList<FilePair> ImportCoChanges(String csvFile) {
        ArrayList<String[]> coChangeLines = readLines(csvFile);

        // Map raw co-change lines to FilePairs
        return coChangeLines.stream().map(cc -> new FilePair(cc[0], cc[1])).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Reads changes from CSV file and aggregates them per file.
     * @param csvFile File to read changes from.
     * @return Changes per file.
     */
    public static ArrayList<ChangeRecord> ImportChanges(String csvFile) {

        ArrayList<String[]> changeLines = readLines(csvFile);
        // Map raw co-change lines to FilePairs
        return changeLines.stream().map(c -> new ChangeRecord(c[0], c[1], c[2])).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Reads all lines except the header row of a csv file.
     * @param csvFile File to read
     * @return lines that were read, excluding the top line
     */
    private static ArrayList<String[]> readLines(String csvFile) {
        String line;
        String cvsSplitBy = CSVExporter.CSV_SEPARATOR;

        ArrayList<String[]> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                // Read raw lines
                lines.add(line.split(cvsSplitBy));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
