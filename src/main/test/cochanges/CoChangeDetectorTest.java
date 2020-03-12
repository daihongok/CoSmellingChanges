package cochanges;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class CoChangeDetectorTest {
    @BeforeAll
    static void BeforeAll() {
        ConfigurationManager.OverrideProperty("ProjectsDirectory","CoChangeDetectTest");
        ConfigurationManager.OverrideProperty("ProjectOwner","RonaldKruizinga");
    }

    @Test
    void CoChangeTest() {

    }

    @AfterAll
    static void AfterAll() {
        ConfigurationManager.RemoveOverriddenProperty("ProjectsDirectory");
        ConfigurationManager.RemoveOverriddenProperty("ProjectOwner");
    }
}
