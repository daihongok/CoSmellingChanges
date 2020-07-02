package system;

import Model.CoChange;
import Model.FileChange;
import Model.FilePair;
import Model.GitProject;
import base.TestDirectoryTest;
import cochanges.ChangeDetector;
import cochanges.CoChangeDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;
import utility.CSVImporter;
import Model.ChangeRecord;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static utility.FileOperations.GetPackageFromFile;
import static org.junit.jupiter.api.Assertions.fail;

class SystemTest extends TestDirectoryTest {

    @Test
    void systemTest() {
        GitProject project = GitProject.CreateFromConfig();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<ObjectId> commitsInOrder = cd.InitialiseChangeHistory(project);
        CoChangeDetector ccd = new CoChangeDetector(commitsInOrder);
        ArrayList<CoChange> coChanges = ccd.findCoChanges(cd);
        coChanges.forEach(c -> c.findPackages(project));

        // Map changes to validation format
        ArrayList<ChangeRecord> actualChanges = new ArrayList<>();
        for (String key : cd.getChangeHistory().keySet()) {
            FileChange file = cd.getChangeHistory().get(key);
            // Loop over the versions in which this file changed
            for (RevCommit changedCommit : file.getCommits()) {
                actualChanges.add(new ChangeRecord(file.getLastPath(),GetPackageFromFile(Paths.get(file.getLastPath()).toString(),project),changedCommit.getId().getName()));
            }
        }
        // Map co-changes to validation format
        ArrayList<FilePair> actualCoChanges = coChanges.stream().map(cc -> new FilePair(cc.getFile1(),cc.getFile2())).collect(Collectors.toCollection(ArrayList::new));

        // Read validation data
        ArrayList<ChangeRecord> expectedChanges = CSVImporter.ImportChanges("resources/testing/changes.csv");
        ArrayList<FilePair> expectedCoChanges = CSVImporter.ImportCoChanges("resources/testing/cochanges.csv");

        // Validation
        //
        var missingChanges = difference(expectedChanges, actualChanges);
        missingChanges.forEach(mc -> fail("Missing change in " + mc.getName() + "(package: " + mc.getPackage() + ") during commit: " + mc.getVersion()));
        var abundantChanges = difference(actualChanges, expectedChanges);
        abundantChanges.forEach(ac -> fail("Abundant change in " + ac.getName() + "(package: " + ac.getPackage() + ") during commit: " + ac.getVersion()));
        var missingCoChanges = difference(expectedCoChanges, actualCoChanges);
        missingCoChanges.forEach(mc -> fail("Missing co-change in " + mc.getFile1() + " and " + mc.getFile2()));
        var abundantCoChanges = difference(actualCoChanges, expectedCoChanges);
        abundantCoChanges.forEach(ac -> fail("Abundant co-change in " + ac.getFile1() + " and " + ac.getFile2()));
    }

    /**
     * Finds elements that are in expected but not in actual.
     * @param expected Expected values
     * @param actual Actual values
     * @param <T> type of objects
     * @return Missing objects
     */
     private <T> ArrayList<T> difference(ArrayList<T> expected, ArrayList<T> actual)  {
         var a = new ArrayList<>(expected);
         var b = new ArrayList<>(actual);
         a.removeAll(b);
         return a;
    }
}
