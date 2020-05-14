package utility;

import cochanges.CoChangeProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility operations on files.
 */
public class FileOperations {
    /**
     * Reads the package of a java file and returns it.
     * @param file
     * @param project
     * @return The package in the form a.b.c
     */
    public static String GetPackageFromFile(String file, CoChangeProject project) {
        // Locate project folder
        File projectDir = new File(project.getProjectLocation());
        // Locate our file in the project folder
        Optional<String> matchingFile = project.getProjectFiles().stream().filter(f -> f.endsWith(file)).findFirst();
        if (matchingFile.isPresent()) {
            return ExtractPackage(new File(matchingFile.get()));
        } else {
            return "";
        }
    }

    private static String ExtractPackage(File file) {
        BufferedReader reader;
        StringBuilder contents;
        try {
            reader = new BufferedReader(new FileReader(file));
            contents = new StringBuilder();
            while (reader.ready()) {
                contents.append(reader.readLine());
            }
            reader.close();

            final String stringContents = contents.toString();
            Pattern pattern = Pattern.compile("package\\s+([\\w.]+);", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(stringContents);
            if (matcher.find()) {
                return matcher.group(1);
            }

        } catch (Exception e) {
            System.out.println(file.toString());
            e.printStackTrace();
        }
        return "";
    }
}
