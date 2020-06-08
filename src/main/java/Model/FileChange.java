package Model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;

public class FileChange {
    private ArrayList<RevCommit> commits;

    public FileChange() {
        commits = new ArrayList<>();
    }
    public ArrayList<RevCommit> getCommits() {
        return commits;
    }


    public void addCommit(RevCommit commit) {
        commits.add(commit);
    }
}
