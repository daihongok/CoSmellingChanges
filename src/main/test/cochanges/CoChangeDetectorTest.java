package cochanges;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.util.ArrayList;

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
        ArrayList<CoChange> coChanges = ccd.getCoChanges(project.getRepository(), project.getGit());
        ccd.storeCoChanges(coChanges,"resources/validation/full-project-test-1.json");
    }

    @AfterAll
    static void AfterAll() {
        ConfigurationManager.RemoveOverriddenProperty("ProjectName");
        ConfigurationManager.RemoveOverriddenProperty("ProjectOwner");
    }
}
