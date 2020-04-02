package export;

import cochanges.CoChange;

import java.io.*;
import java.util.ArrayList;

/**
 * Provides an interface to persist co-change data in to files.
 */
public class CSVExporter {
    /**
     * Separator used in between values on one line in the CSV file.
     */
    private static final String CSV_SEPARATOR = ",";
    /**
     * Stores the list of co-changes in the given file.
     * @param filePath CSV file to store data in
     * @param coChanges Co-changes to store
     */
    public static void writeToCSV(String filePath, ArrayList<CoChange> coChanges) {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            // Write header line
            StringBuffer headerLine = new StringBuffer();
            headerLine.append("file1");
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("file2");
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("threshold");
            bw.write(headerLine.toString());
            bw.newLine();
            // Write data records
            for (CoChange coChange : coChanges)
            {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(coChange.getFile1());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getFile2());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getCoVersions().size());
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
