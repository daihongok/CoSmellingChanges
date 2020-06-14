package cochanges;

import Config.ConfigurationManager;
import Model.FileChange;
import Model.GitProject;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import utility.FileOperations;

import java.io.IOException;
import java.util.*;

/**
 * Calculates metrics related to file changes:
 * - PCCC: Percentage of Commits a Class has Changed
 * - PCPC: Percentage of Commits a Package has Changed
 * - TACH: Total Amount of Changes
 * - FRCH: Frequency of Changes
 * - CHO: Change has Occurred
 */
public class ChangeDetector  {
    /**
     * The similarity measured as a percentage of the bytes between two files to count them as a rename.
     * Default value used by git is 60.
     */
    private static final int RENAME_SCORE = 50;
    /**
     * The maximum number of files to compare within a rename to not reduce performance.
     * Default value used by git is 1000.
     */
    private static final int RENAME_LIMIT = 500;

    Map<String, FileChange> getChangeHistory() {
        return changeHistory;
    }

    private Map<String, FileChange> changeHistory;

    private DiffFormatter diffFormatter;
    private List<DiffEntry> entries;

    public ChangeDetector() {
        this.changeHistory = new HashMap<>(1000);
    }

    public ArrayList<ObjectId> InitialiseChangeHistory(GitProject project){
        ArrayList<ObjectId> commitsInOrder = new ArrayList<>();
        try {
            Repository repository = project.getRepository();
            RevWalk walk = project.getWalk();

            int commitsAnalyzed = 0;

            for (RevCommit currentCommit : walk) {
                // Stop when we hit the cap of commits to analyze.
                if (commitsAnalyzed == ConfigurationManager.getMaxAmountOfCommits()) {
                    break;
                }
                commitsInOrder.add(currentCommit.getId());

                if (currentCommit.getParentCount() == 0) { // Last commit has no parent.
                    continue;
                }

                RevCommit directParent = currentCommit.getParent(0);

                TreeWalk parentWalk = new TreeWalk(repository);
                parentWalk.setRecursive(true);
                parentWalk.reset(directParent.getTree());

                TreeWalk childWalk = new TreeWalk(repository);
                childWalk.setRecursive(true);
                childWalk.reset(currentCommit.getTree());

                HashSet<String> files = FileOperations.GetFileUnion(parentWalk, childWalk);
                this.calculateFileChanges(files, repository, directParent, currentCommit);

                commitsAnalyzed++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return commitsInOrder;
    }

    private void calculateFileChanges(HashSet<String> files, Repository repo, RevCommit parentCommit, RevCommit childCommit) {
        ObjectId parentCommitId = parentCommit.getId();
        ObjectId childCommitId = childCommit.getId();

        initDiff(repo,parentCommitId,childCommitId);
        entries.forEach(entry -> {
            if(files.contains(entry.getNewPath())){
                String pathFileStr = entry.getNewPath();

                String key;
                FileChange fileChange;
                key = pathFileStr; // store changes per file
                switch (entry.getChangeType()) {
                    case ADD:
                    case MODIFY:
                        fileChange = changeHistory.getOrDefault(key, new FileChange());
                        fileChange.addCommit(childCommit);
                        changeHistory.put(key, fileChange);
                        break;
                    case RENAME:
                        // Remove old entry and store the changes under the new one.
                        fileChange = changeHistory.getOrDefault(entry.getOldPath(), new FileChange());
                        fileChange.addCommit(childCommit);
                        changeHistory.put(key, fileChange);
                        changeHistory.remove(entry.getOldPath());
                        break;
                    default:
                        break;
                }
           }
        });
    }

    /**
     * Computes the list of DiffEntry that contains all the differences between the 2 commits.
     * Note that this implementation only returns the differences between the two given commit, ignoring any
     * commit in between.
     * @param repo The repository in which the commits are.
     * @param parent The parent commit to which needs to be compared.
     * @param child The child commit that needs to be compared to the parent commit.
     */
    private void initDiff(Repository repo, ObjectId parent, ObjectId child) {
        if (diffFormatter == null) {
            diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(repo);
            diffFormatter.setDetectRenames(true);
        }
        var renameDetector = diffFormatter.getRenameDetector();
        renameDetector.setRenameScore(RENAME_SCORE);
        renameDetector.setRenameLimit(RENAME_LIMIT);
        try {
            entries = diffFormatter.scan(parent, child);
        } catch (IOException e) {
            System.out.println("Could not perform diff between parent commit and child.");
            entries = new ArrayList<>();
        }
        diffFormatter.close();
    }

}
