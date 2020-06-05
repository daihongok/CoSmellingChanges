package utility;

import cochanges.CoChangeProject;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility operations on files.
 */
public class FileOperations {
    private static HashMap<String, String> cache = new HashMap<>();
    /**
     * Reads the package of a java file and returns it.
     * @param file
     * @param project
     * @return The package in the form a.b.c
     */
    public static String GetPackageFromFile(String file, CoChangeProject project) {
        // Locate project folder
        File projectDir = new File(project.getProjectLocation());


        if(cache.containsKey(file)){
            return cache.get(file);
        }
        // Locate our file in the project folder
        Optional<String> matchingFile = project.getProjectFiles().stream().filter(f -> f.endsWith(file)).findFirst();
        if (matchingFile.isPresent()) {
            String packagePath = ExtractPackage(new File(matchingFile.get()));
            cache.put(file, packagePath);
            return packagePath;
        } else {
            cache.put(file, "");
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
