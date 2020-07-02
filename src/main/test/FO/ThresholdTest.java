package FO;

import Config.ConfigurationManager;
import Model.CoChange;
import Model.GitProject;
import base.TestDirectoryTest;
import cochanges.ChangeDetector;
import cochanges.CoChangeDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThresholdTest extends TestDirectoryTest {
    /**
     * Validates whether the FO threshold works correctly.
     */
    @Test
    void ThresholdFilterTest() {
        ConfigurationManager.OverrideProperty("CoChanges.Threshold","24");

        GitProject project = GitProject.CreateFromConfig();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<ObjectId> commitsInOrder = cd.InitialiseChangeHistory(project);
        CoChangeDetector ccd = new CoChangeDetector(commitsInOrder);
        ArrayList<CoChange> coChanges = ccd.findCoChanges(cd);
        coChanges.forEach(c -> c.findPackages(project));

        assertEquals(1, coChanges.size(), "Wrong amount of co-changes for threshold 24.");
        var cc = coChanges.get(0);
        assertEquals("Cyclic.java",  cc.getFile1());
        assertEquals("ArtifactB.java", cc.getFile2());

    }
}
