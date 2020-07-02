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

class DistanceFilterTests extends TestDirectoryTest {
    /**
     * Validates whether the FO commit distance works correctly.
     */
    @Test
    void CommitDistanceFilterTest() {
        ConfigurationManager.OverrideProperty("CoChanges.MaxHoursBetweenCommits","20");
        ConfigurationManager.OverrideProperty("CoChanges.MaxCommitsBetweenCommits","1");

        GitProject project = GitProject.CreateFromConfig();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<ObjectId> commitsInOrder = cd.InitialiseChangeHistory(project);
        CoChangeDetector ccd = new CoChangeDetector(commitsInOrder);
        ArrayList<CoChange> coChanges = ccd.findCoChanges(cd);
        coChanges.forEach(c -> c.findPackages(project));
        var fileChanges = cd.getChangeHistory();
        var overlap = ccd.relatedChanges(fileChanges.get("TestingRepository/src/Independent.java").getCommits(), fileChanges.get("TestingRepository/src/Cyclic.java").getCommits());
        assertEquals(4, overlap.size(), "Wrong amount of matches");

        assertEquals(overlap.get(0).getItem1().getName(),"d5b146515771b9ab47cfbf6e29292fbf5efda94a");
        assertEquals(overlap.get(0).getItem2().getName(),"43eb1d125b8bb8af897d5e01238a257c43ea3ea5");

        assertEquals(overlap.get(1).getItem1().getName(),"95752c69f8d84ba3187fdc2616a965fb74c7f29f");
        assertEquals(overlap.get(1).getItem2().getName(),"95752c69f8d84ba3187fdc2616a965fb74c7f29f");

        assertEquals(overlap.get(2).getItem1().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
        assertEquals(overlap.get(2).getItem2().getName(),"95752c69f8d84ba3187fdc2616a965fb74c7f29f");

        assertEquals(overlap.get(3).getItem1().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
        assertEquals(overlap.get(3).getItem2().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
    }

    /**
     * Validates whether the FO time distance works correctly.
     */
    @Test
    void TimeDistanceFilterTest() {
        ConfigurationManager.OverrideProperty("CoChanges.MaxCommitsBetweenCommits","100");
        ConfigurationManager.OverrideProperty("CoChanges.MaxHoursBetweenCommits","1");

        GitProject project = GitProject.CreateFromConfig();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<ObjectId> commitsInOrder = cd.InitialiseChangeHistory(project);
        CoChangeDetector ccd = new CoChangeDetector(commitsInOrder);
        ArrayList<CoChange> coChanges = ccd.findCoChanges(cd);
        coChanges.forEach(c -> c.findPackages(project));
        var fileChanges = cd.getChangeHistory();
        var overlap = ccd.relatedChanges(fileChanges.get("TestingRepository/src/Independent.java").getCommits(), fileChanges.get("TestingRepository/src/Dependent.java").getCommits());
        assertEquals(5, overlap.size(), "Wrong amount of matches");

        assertEquals(overlap.get(0).getItem1().getName(),"d5b146515771b9ab47cfbf6e29292fbf5efda94a");
        assertEquals(overlap.get(0).getItem2().getName(),"d5b146515771b9ab47cfbf6e29292fbf5efda94a");

        assertEquals(overlap.get(1).getItem1().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
        assertEquals(overlap.get(1).getItem2().getName(),"d5b146515771b9ab47cfbf6e29292fbf5efda94a");

        assertEquals(overlap.get(2).getItem1().getName(),"95752c69f8d84ba3187fdc2616a965fb74c7f29f");
        assertEquals(overlap.get(2).getItem2().getName(),"d5b146515771b9ab47cfbf6e29292fbf5efda94a");

        assertEquals(overlap.get(3).getItem1().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
        assertEquals(overlap.get(3).getItem2().getName(),"95752c69f8d84ba3187fdc2616a965fb74c7f29f");

        assertEquals(overlap.get(4).getItem1().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
        assertEquals(overlap.get(4).getItem2().getName(),"6659aef2d3b7820dfb183747fd36b17598418299");
    }
}
