
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final String NAME = "freqOfChanges";
    /**
     * The similarity measured as a percentage of the bytes between two files to count them as a rename.
     * Default value used by git is 60.
     */
    protected static final int RENAME_SCORE = 50;
    /**
     * The maximum number of files to compare within a rename to not reduce performance.
     * Default value used by git is 1000.
     */
    protected static final int RENAME_LIMIT = 500;

    public Map<String, ArrayList<ObjectId>> getChangeHistory() {
        return changeHistory;
    }

    private Map<String, ArrayList<ObjectId>> changeHistory;

    private DiffFormatter diffFormatter;
    private List<DiffEntry> entries;

    public ChangeDetector(String name) {
        this.changeHistory = new HashMap<>(1000);
    }

    protected void calculate(String pathFileStr, Repository repo, ObjectId parent, ObjectId child) {
        initDiff(repo,parent,child);
        var changeOpt = getDiffOf(pathFileStr);

        var hasChanged = false;
        String key;
        if (changeOpt.isPresent()){
            var change = changeOpt.get();
            ArrayList<ObjectId> changedVersions;
            key = pathFileStr; // store changes per file
            switch (change.getChangeType()) {
                case ADD:
                case MODIFY:
                    hasChanged = true;
                    changedVersions = changeHistory.getOrDefault(key, new ArrayList<>());
                    changedVersions.add(child);
                    changeHistory.put(key, changedVersions);
                    break;
                case COPY:
                case RENAME:
                    hasChanged = true;
                    //changedVersions = changeHistory.remove(change.getOldPath()); //TODO is this required for copy?
                    //changedVersions = changedVersions == null ? new ArrayList<>() : changedVersions;
                    //changeHistory.put(key, changedVersions); //TODO what do we do here?
                    break;
                case DELETE:
                default:
                    break;
            }
        }
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

    /**
     * Looks in the diff entry of between the current commits to find the given path.
     * @param pathSuffix the path to use as a suffix.
     * @return an optional containing a DiffEntry if any path was matched.
     */
    private Optional<DiffEntry> getDiffOf(String pathSuffix){
        return entries.stream().filter(e -> e.getNewPath().endsWith(pathSuffix)).findFirst();
    }
}
