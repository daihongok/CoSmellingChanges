package changes;

import Model.GitProject;
import base.TestDirectoryTest;
import cochanges.ChangeDetector;
import org.eclipse.jgit.lib.AnyObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MergeTest extends TestDirectoryTest {
    /**
     * Tests whether merge commits are indeed ignored.
     */
    @Test
    void MergeFilterTest() {
        GitProject project = GitProject.CreateFromConfig();

        ChangeDetector cd = new ChangeDetector();
        ArrayList<String> commitsInOrder = cd.InitialiseChangeHistory(project).stream().map(AnyObjectId::getName).collect(Collectors.toCollection(ArrayList::new));
        assertFalse(commitsInOrder.contains("ce9d4ff1d60ac1260267d6bbfaad6147e95f7efb"));
        assertFalse(commitsInOrder.contains("1847c8162d0b69b17b40f3a666b7bc032e14e648"));
        assertFalse(commitsInOrder.contains("1f4f78178c033f759582781dd8b1b51a4f979770"));
        assertFalse(commitsInOrder.contains("0de081c0e59e46d724db44ecdbbf0adeb310ef47"));
    }
}
