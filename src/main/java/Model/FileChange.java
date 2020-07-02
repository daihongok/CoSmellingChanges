package Model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;

public class FileChange {
    private ArrayList<RevCommit> commits;
    /**
     * Last location of the file during its history (path/name.java)
     */
    private String lastPath;

    public FileChange() {
        commits = new ArrayList<>();
    }

    public ArrayList<RevCommit> getCommits() {
        return commits;
    }


    public void addCommit(RevCommit commit) {
        commits.add(commit);
    }

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String path) {
        lastPath = path;
    }
}
