package base;

import Config.ConfigurationManager;
import org.junit.jupiter.api.BeforeAll;

public class TestDirectoryTest {
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
}
