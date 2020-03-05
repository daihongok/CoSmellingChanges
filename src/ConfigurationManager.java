import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
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
}
