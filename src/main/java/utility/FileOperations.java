package utility;

import Config.SourcesManager;
import Model.GitProject;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
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
    public static String GetPackageFromFile(String file, GitProject project) {
        // Locate project folder
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

    /**
     * Helper method for finding the union of two file collections.
     * @param parentWalk
     * @param childWalk
     * @return
     */
    public static HashSet<String> GetFileUnion(TreeWalk parentWalk, TreeWalk childWalk) {

        HashSet<String> files = new HashSet<>();

        try {

            while (parentWalk.next()) {
                String path = parentWalk.getPathString();
                if (path.endsWith(".java") && pathInSources(path)) {
                    files.add(path);
                }
            }
            while (childWalk.next()) {
                String path = childWalk.getPathString();
                if (path.endsWith(".java") && pathInSources(path)) {
                    files.add(path);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    private static boolean pathInSources(String path){
        return SourcesManager.getListOfDirectories().stream().anyMatch(
                source -> path.startsWith(source)
        );
    }
}
