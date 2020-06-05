package cochanges;

import Config.ConfigurationManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SourcesManager {
    private static Properties properties;

    /**
     * The following hash map contains overrides for the configured values found in the resource files.
     * This is used for automated testing to not interfere with the right configuration.
     */
    private static List<String> directories;

    /**
     * Gets the directory in which projects are stored that are analyzed.
     * @return Directory without slash at the end.
     */
    public static List<String> getListOfDirectories() {
        if (properties == null) {
            try {
                properties = new Properties();

                InputStream input = new FileInputStream("execution-scripts/sourceproperties/"+ ConfigurationManager.getProjectName()+"/sources.properties");
                // load a properties file
                properties.load(input);
                String includes = properties.getProperty("sources.include");
                directories = Arrays.asList(includes.split(":"));

            } catch (IOException | NullPointerException e) {
                directories = new ArrayList<>() {};
                directories.add("");
            }
        }
        // Return the override value if it exists. Otherwise read it from the resource file.
        return directories;
    }

}
