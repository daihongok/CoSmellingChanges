package Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ConfigurationManager {
    private static Properties properties;

    /**
     * The following hash map contains overrides for the configured values found in the resource files.
     * This is used for automated testing to not interfere with the right configuration.
     */
    private static HashMap<String, String> testProperties = new HashMap<>();

    /**
     * Overrides a config property for testing purposes.
     * @param key Key found in the config file.
     * @param value Value to assign to this key during runtime.
     */
    public static void OverrideProperty(String key, String value) {
        testProperties.put(key,value);
    }

    /**
     * Removes an override for a config property.
     * @param key Config property to set back to its default found in the file.
     */
    public static void RemoveOverriddenProperty(String key) {
        testProperties.remove(key);
    }

    /**
     * Loads the config file if needed and retrieves the required value.
     * @param propertyKey What property  to retrieve.
     * @return property value.
     */
    private static String getProperty(String propertyKey) {
        if (properties == null) {
            try {
                properties = new Properties();

                InputStream input = new FileInputStream("resources/config.properties");
                // load a properties file
                properties.load(input);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return the override value if it exists. Otherwise read it from the resource file.
        return testProperties.containsKey(propertyKey) ? testProperties.get(propertyKey) : properties.getProperty(propertyKey);
    }

    /**
     * Gets the directory in which projects are stored that are analyzed.
     * @return Directory without slash at the end.
     */
    public static String getProjectsDirectory() {
        return getProperty("ProjectsDirectory");
    }

    /**
     * Gets the name of the project to be analyzed. Needs to exactly match the name on Git.
     * @return Project's name.
     */
    public static String getProjectName() {
        return getProperty("ProjectName");
    }

    /**
     * Gets the name of the project's owner to be analyzed. Needs to exactly match the name on Git.
     * @return Project's name.
     */
    public static String getProjectOwner() {
        return getProperty("ProjectOwner");
    }

    /**
     * Gets the branch being analyzed.
     * @return The branch.
     */
    public static String getProjectBranch() {
        return getProperty("ProjectBranch");
    }

    /**
     * Gets the last commit from which to back track in to the project's history.
     * @return The commit id (e.g. "HEAD").
     */
    public static String getLastCommit() {
        return getProperty("LastCommit");
    }

    /**
     * Gets whether commits at different moments can be cochanges.
     * @return to consider a time factor or not.
     */
    public static Boolean getConsiderCommitsOverTime(){
        return Boolean.parseBoolean(getProperty("CoChanges.ConsiderCommitsOverTime"));
    }
    /**
     * Gets the maximum amount of days between changes to still be marked as co-changing.
     * @return The number of days.
     */
    public static int getMaxCommitsBetweenCommits() {
        return Integer.parseInt(getProperty("CoChanges.MaxCommitsBetweenCommits"));
    }
    /**
     * Gets the maximum amount of days between changes to still be marked as co-changing.
     * @return The number of days.
     */
    public static int getMaxHoursBetweenCommits() {
        return Integer.parseInt(getProperty("CoChanges.MaxHoursBetweenCommits"));
    }

    /**
     * Max amount of commits to analyze of the project.
     * @return Amount of commits.
     */
    public static int getMaxAmountOfCommits() {
        return Integer.parseInt(getProperty("CoChanges.MaxAmountOfCommits"));
    }

    /**
     * Minimum amount of overlapping changes to count as co-changing.
     * @return Amount of overlapping changes.
     */
    public static int getCoChangeThreshold() {
        return Integer.parseInt(getProperty("CoChanges.Threshold"));
    }
}
