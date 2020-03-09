import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;

public class FileChange {
    private ArrayList<RevCommit> commits;
    private long linesChanged;

    public FileChange() {
        commits = new ArrayList<>();
        linesChanged = 0;
    }
    public ArrayList<RevCommit> getCommits() {
        return commits;
    }

    public long getLinesChanged() {
        return linesChanged;
    }

    public void addChangedLines(long linesChanged) {
        this.linesChanged += linesChanged;
    }

    public void addCommit(RevCommit commit) {
        commits.add(commit);
    }
}
