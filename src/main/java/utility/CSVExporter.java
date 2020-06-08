package utility;

import Model.CoChange;
import Config.ConfigurationManager;
import Model.FilePair;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides an interface to persist co-change data in to files.
 */
public class CSVExporter {
    /**
     * Separator used in between values on one line in the CSV file.
     */
    private static final String CSV_SEPARATOR = ",";
    private static HashMap<String,Long> cache = new HashMap<>();
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
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("package1");
            headerLine.append(CSV_SEPARATOR);
            headerLine.append("package2");
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
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getPackage1());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(coChange.getPackage2());
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
     * Creates the given file and adds the header row so afterwards we can just append content.
     * @param filePath File to create and prepare for file pairs.
     */
    public static void createFileAndHeader(String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            // Write header line
            String headerLine = "file1" +
                    CSV_SEPARATOR +
                    "file2" +
                    CSV_SEPARATOR +
                    "file1Size"+
                    CSV_SEPARATOR +
                    "file2Size" +
                    CSV_SEPARATOR +
                    "package1" +
                    CSV_SEPARATOR +
                    "package2";
            bw.write(headerLine);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Exports a list of file combinations to a file.
     * @param filePath csv file to store the pairs in
     * @param pairs file pairs to store
     */
    public static void storeFilePairs(String filePath, ArrayList<FilePair> pairs) {
        try
        {
            FileWriter fw = new FileWriter(filePath,true);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write data records
            for (FilePair nonCoChange : pairs)
            {
                long sizeOne = getFileSize(nonCoChange.getFile1());
                long sizeTwo = getFileSize(nonCoChange.getFile2());
                String oneLine = nonCoChange.getFile1() +
                    CSV_SEPARATOR +
                    nonCoChange.getFile2()+
                    CSV_SEPARATOR +
                    sizeOne +
                    CSV_SEPARATOR +
                    sizeTwo +
                    CSV_SEPARATOR +
                    nonCoChange.getPackage1() +
                    CSV_SEPARATOR +
                    nonCoChange.getPackage2();
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
        if(cache.containsKey(filepath)){
            return cache.get(filepath);
        }
        Path path = Paths.get("projects/" + ConfigurationManager.getProjectName() + "/" + filepath);
        try {
            Long fileCount = Files.lines(path, Charset.forName("windows-1251")).count();
            cache.put(filepath, fileCount);
            return fileCount;
        } catch (IOException e) {
            cache.put(filepath, -1L);
            return -1;
        } catch (UncheckedIOException e) {
            System.out.println("File size of this file could not be calculated:" + filepath);
            cache.put(filepath, -1L);
            return -1;
        }
    }
}
