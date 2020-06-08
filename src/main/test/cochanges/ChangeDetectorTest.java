package cochanges;

import Config.ConfigurationManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ChangeDetectorTest {
    private final ChangeDetector changeDetector = new ChangeDetector();

    @BeforeAll
    static void BeforeAll() {
        ConfigurationManager.OverrideProperty("ProjectsDirectory","CoChangeDetectTest");
    }

    @AfterAll
    static void AfterAll() {
        ConfigurationManager.RemoveOverriddenProperty("ProjectsDirectory");
    }
}
