package cochanges;

import utility.Tuple;

import java.util.ArrayList;

public class CoChangeExport {

    private String file1;
    private String file2;
    private Tuple<String>[] coChangeCommitPairs;

    public String getFile1() {
        return file1;
    }

    public String getFile2() {
        return file2;
    }

    public Tuple<String>[] getCoChangeCommitPairs() {
        return coChangeCommitPairs;
    }

    public CoChangeExport(CoChange coChange) {
        file1 = coChange.getFile1();
        file2 = coChange.getFile2();
        coChangeCommitPairs = coChange.getCoVersions().stream()
                .map(c -> new Tuple<String>(c.getItem1().getId().toString(),c.getItem2().getId().toString())).toArray(Tuple[]::new);
    }

    public String toString() {
        return String.format("File 1: %s | File 2: %s | Commits: %d", file1, file2, coChangeCommitPairs.length);
    }
}
