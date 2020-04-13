package export;

import cochanges.CoChange;
import cochanges.ConfigurationManager;
import utility.Tuple;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public static void storeCoChanges(String filePath, ArrayList<CoChange> coChanges) {
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
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("startdate");
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("enddate");
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
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getStartDate());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getEndDate());
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

    /**
     * Exports a list of file combinations to a file.
     * @param filePath csv file to store the pairs in
     * @param pairs file pairs to store
     */
    public static void storeFilePairs(String filePath, ArrayList<Tuple<String>> pairs) {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            // Write header line
            String headerLine = "file1" +
                    CSV_SEPARATOR +
                    "file2" +
                    CSV_SEPARATOR +
                    "file1Size"+
                    CSV_SEPARATOR +
                    "file2Size";
            bw.write(headerLine);
            bw.newLine();
            // Write data records
            for (Tuple<String> nonCoChange : pairs)
            {
                long sizeOne = getFileSize(nonCoChange.getItem1());
                long sizeTwo = getFileSize(nonCoChange.getItem2());
                String oneLine = nonCoChange.getItem1() +
                    CSV_SEPARATOR +
                    nonCoChange.getItem2()+
                    CSV_SEPARATOR +
                    sizeOne +
                    CSV_SEPARATOR +
                    sizeTwo;
                bw.write(oneLine);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long getFileSize(String filepath){
        Path path = Paths.get("projects/" + ConfigurationManager.getProjectName() + "/" + filepath);
        try {
            return Files.lines(path).count();
        } catch (IOException e) {
            return -1;
        }
    }
}
