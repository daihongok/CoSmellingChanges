package cochanges;

import org.eclipse.jgit.revwalk.RevCommit;
import utility.Tuple;

import java.io.File;
import java.util.ArrayList;

public class CoChange {
    private File file1;
    private File file2;
    private ArrayList<Tuple<RevCommit>> coVersions;

    CoChange(String file1, String file2, ArrayList<Tuple<RevCommit>> coVersions) {
        this.file1 = new File(file1);
        this.file2 = new File(file2);
        this.coVersions = coVersions;
    }

    public String toString() {
        return "CoCh: " + file1.getName() + " and " + file2.getName() + ". Overlap: \r\n"
                // Add the commit pairs the account to the co-change
                + coVersions.stream()
                            .map(tuple -> "("+tuple.getItem1()+", "+tuple.getItem2()+")\r\n")
                            .reduce("",(curr,next) -> curr + next);
    }
}
