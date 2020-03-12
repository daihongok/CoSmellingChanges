package main.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class ConfigurationManager {
    private static Properties properties;

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
        return properties.getProperty(propertyKey);
    }

    /**
     * Gets the directory in which projects are stored that are analyzed.
     * @return Directory without slash at the end.
     */
    static String getProjectsDirectory() {
        return getProperty("ProjectsDirectory");
    }

    /**
     * Gets the name of the project to be analyzed. Needs to exactly match the name on Git.
     * @return Project's name.
     */
    static String getProjectName() {
        return getProperty("ProjectName");
    }

    /**
     * Gets the name of the project's owner to be analyzed. Needs to exactly match the name on Git.
     * @return Project's name.
     */
    static String getProjectOwner() {
        return getProperty("ProjectOwner");
    }

    /**
     * Gets the branch being analyzed.
     * @return The branch.
     */
    static String getProjectBranch() {
        return getProperty("ProjectBranch");
    }

    /**
     * Gets the maximum amount of days between changes to still be marked as co-changing.
     * @return The number of days.
     */
    static int getMaxDaysBetweenCoChanges() {
        return Integer.parseInt(getProperty("CoChanges.MaxDaysBetweenCommits"));
    }

    /**
     * Max amount of commits to analyze of the project.
     * @return Amount of commits.
     */
    static int getMaxAmountOfCommits() {
        return Integer.parseInt(getProperty("CoChanges.MaxAmountOfCommits"));
    }

    /**
     * Minimum amount of overlapping changes to count as co-changing.
     * @return Amount of overlapping changes.
     */
    static int getCoChangeThreshold() {
        return Integer.parseInt(getProperty("CoChanges.Threshold"));
    }
}
