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
        ConfigurationManager.OverrideProperty("LastCommit","6109deaca7150bcf6703940f03dcd5ff13f6f7ab");
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
