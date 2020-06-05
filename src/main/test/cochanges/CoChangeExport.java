package cochanges;

import Model.CoChange;
import com.google.gson.Gson;
import utility.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class CoChangeExport {

    private String file1;
    private String file2;
    private Tuple<String>[] coChangeCommitPairs;

    String getFile1() {
        return file1;
    }

    String getFile2() {
        return file2;
    }

    Tuple<String>[] getCoChangeCommitPairs() {
        return coChangeCommitPairs;
    }

    CoChangeExport(CoChange coChange) {
        file1 = coChange.getFile1();
        file2 = coChange.getFile2();
        coChangeCommitPairs = coChange.getCoVersions().stream()
                .map(c -> new Tuple<String>(c.getItem1().getId().toString(),c.getItem2().getId().toString())).toArray(Tuple[]::new);
    }

    public String toString() {
        return String.format("File 1: %s | File 2: %s | Commits: %d", file1, file2, coChangeCommitPairs.length);
    }

    /**
     * Parses the JSON contents of the file to an array of cochange exports.
     * @param filePath File to read.
     * @return Array of CoChangeExports
     */
    static CoChangeExport[] readCoChangesFromFile(String filePath) {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(content,CoChangeExport[].class);
    }
}
