package cochanges;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;

class CoChangeDetectorTest {
    CoChangeDetector ccd = new CoChangeDetector();

    @BeforeAll
    static void BeforeAll() {
        ConfigurationManager.OverrideProperty("ProjectName","CoChangeDetectTest");
        ConfigurationManager.OverrideProperty("ProjectOwner","RonaldKruizinga");
        ConfigurationManager.OverrideProperty("ProjectBranch","refs/heads/master");
        ConfigurationManager.OverrideProperty("LastCommit","ce9d4ff1d60ac1260267d6bbfaad6147e95f7efb");
    }

    @Test
    void CoChangeTest() {
        CoChangeProject project = CoChangeProject.CreateFromConfig();
        ArrayList<CoChange> coChanges = ccd.getCoChanges(project.getRepository(), project.getGit());
        var x = 5;
    }

    @AfterAll
    static void AfterAll() {
        ConfigurationManager.RemoveOverriddenProperty("ProjectName");
        ConfigurationManager.RemoveOverriddenProperty("ProjectOwner");
    }
}
