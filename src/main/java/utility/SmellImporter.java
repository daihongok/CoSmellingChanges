package utility;

import com.google.gson.Gson;
import Model.Smell;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SmellImporter {
    private static int INDEX_COMPONENT_TYPE = 13;
    private static int INDEX_AFFECTED_COMPONENTS = 34;
    public static ArrayList<Smell> LoadSmellsFromCSVFile(boolean classSmellsOnly) {
        Path smellFile = Paths.get("resources/input/smell-characteristics-consecOnly.csv");
        // Smells are collected in this list:
        ArrayList<Smell> smells = new ArrayList<>();

        // Read csv file per line and map each line to a smell
        try (BufferedReader br = Files.newBufferedReader(smellFile,
                StandardCharsets.US_ASCII)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                String[] attributes = line.split(",");
                // Only add the smell if it's a class smell depending on the passed config.
                if (!classSmellsOnly || attributes[INDEX_COMPONENT_TYPE].equals("class")) {
                    Smell smell = createSmellFromCSVLine(attributes);
                    smells.add(smell);
                }

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return smells;
    }

    private static Smell createSmellFromCSVLine(String[] attributes) {
        Gson gson = new Gson();
        String affectedComponentsList = String.join("",Arrays.copyOfRange(attributes, INDEX_AFFECTED_COMPONENTS, attributes.length));
        String affectedComponentsValues = affectedComponentsList.substring(2,affectedComponentsList.length()-2);
        String[] affectedComponents = affectedComponentsValues.split(" ");
        affectedComponents = Arrays.stream(affectedComponents).map(String::trim).toArray(String[]::new);
        // map to file name/class name
        affectedComponents = Arrays.stream(affectedComponents)
                .map(ac -> ac.split("\\."))
                .map(parts -> parts[parts.length-1])
                .map(classname -> classname + ".java")
                .toArray(String[]::new);
        return new Smell(new ArrayList<>(Arrays.asList(affectedComponents)));
    }
}
