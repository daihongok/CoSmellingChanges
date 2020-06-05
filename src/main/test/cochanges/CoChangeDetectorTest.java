package cochanges;

import Config.ConfigurationManager;
import Model.CoChange;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import utility.FileOperations;
import utility.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

class CoChangeDetectorTest {
    CoChangeDetector ccd = new CoChangeDetector();

    @BeforeAll
    static void BeforeAll() {
        ConfigurationManager.OverrideProperty("ProjectName","CoChangeDetectTest");
        ConfigurationManager.OverrideProperty("ProjectOwner","RonaldKruizinga");
        ConfigurationManager.OverrideProperty("ProjectBranch","refs/heads/master");
        ConfigurationManager.OverrideProperty("LastCommit","HEAD");
        ConfigurationManager.OverrideProperty("CoChanges.ConsiderCommitsOverTime","true");
        ConfigurationManager.OverrideProperty("CoChanges.MaxCommitsBetweenCommits","100");
        ConfigurationManager.OverrideProperty("CoChanges.MaxDaysBetweenCommits","0");
        ConfigurationManager.OverrideProperty("CoChanges.MaxAmountOfCommits","100");
        ConfigurationManager.OverrideProperty("CoChanges.Threshold","1");

    }

    @Test
    void CoChangeTest() {
        CoChangeProject project = CoChangeProject.CreateFromConfig();
        ArrayList<CoChange> coChanges = ccd.getCoChanges(project);
        CoChangeExport[] foundCoChanges = coChanges.stream().map(CoChangeExport::new).toArray(CoChangeExport[]::new);
        CoChangeExport[] actualCoChanges = CoChangeExport.readCoChangesFromFile("resources/validation/full-project-test-1.json");
        // First check if the pairs of both lists are identical
        Tuple[] foundFilePairs = Arrays.stream(foundCoChanges).map(fcc -> new Tuple(fcc.getFile1(),fcc.getFile2())).toArray(Tuple[]::new);
        Collection<Tuple> foundFilePairsCollection = new LinkedList<>(Arrays.asList(foundFilePairs));
        Tuple[] actualFilePairs = Arrays.stream(actualCoChanges).map(fcc -> new Tuple(fcc.getFile1(),fcc.getFile2())).toArray(Tuple[]::new);
        Collection<Tuple> actualFilePairsCollection = new LinkedList<>(Arrays.asList(actualFilePairs));

        // Check for too many co-changes
        foundFilePairsCollection.removeAll(Arrays.asList(actualFilePairs));
        if (foundFilePairsCollection.size() > 0) {
            String excessPairs = String.join("",foundFilePairsCollection.stream().map(Tuple::toString).toArray(String[]::new));
            fail("Too many co-changes reported. Excess co-changes: " + excessPairs);
        }

        // Check for too little co-changes
        actualFilePairsCollection.removeAll(Arrays.asList(foundFilePairs));
        if (actualFilePairsCollection.size() > 0) {
            String missingPairs = String.join("",actualFilePairsCollection.stream().map(Tuple::toString).toArray(String[]::new));
            fail("Too little co-changes reported. Missing co-changes: " + missingPairs);
        }

        // Here we now both lists contain the same elements, but not necessarily the same commits per co-change.
        // Validate the equality of the commit list.
        for (CoChangeExport foundCoChange : foundCoChanges) {
            // Find co-change in actual co-changes
            Optional<CoChangeExport> actualCoChange = Stream.of(actualCoChanges)
                                                .filter(ac -> ac.getFile1().equals(foundCoChange.getFile1())
                                                        && ac.getFile2().equals(foundCoChange.getFile2())).findFirst();
            assert(actualCoChange.isPresent());
            // Check for too many commits
            Collection<Tuple<String>> foundCommits =  new LinkedList<>(Arrays.asList(foundCoChange.getCoChangeCommitPairs()));
            foundCommits.removeAll(Arrays.asList(actualCoChange.get().getCoChangeCommitPairs()));
            if (foundCommits.size() > 0) {
                String excessPairs = String.join("",foundCommits.stream().map(Tuple::toString).toArray(String[]::new));
                fail("Too many commits reported for " + foundCoChange.toString() + ": " + excessPairs);
            }
            // Check for too little commits
            Collection<Tuple<String>> actualCommits =  new LinkedList<>(Arrays.asList(actualCoChange.get().getCoChangeCommitPairs()));
            actualCommits.removeAll(Arrays.asList(foundCoChange.getCoChangeCommitPairs()));
            if (actualCommits.size() > 0) {
                String missingPairs = String.join("",actualFilePairsCollection.stream().map(Tuple::toString).toArray(String[]::new));
                fail("Too little commits reported for " + foundCoChange.toString() + ": " + missingPairs);
            }
        }

    }

    @AfterAll
    static void AfterAll() {
        ConfigurationManager.RemoveOverriddenProperty("ProjectName");
        ConfigurationManager.RemoveOverriddenProperty("ProjectOwner");
    }




    /*
    * Generating new JSON file:
    * //ccd.storeCoChanges(exports,"resources/validation/full-project-test-1.json");
    * */
}
