package org.proxy4j.core.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.proxy4j.core.reflect.BasicMethodExtractorTest;
import org.proxy4j.core.reflect.InheritableMethodExtractorTest;

/**
 * @author Brennan Spies
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        BasicMethodExtractorTest.class,
        InheritableMethodExtractorTest.class
})
public class CoreReflectionSuite {
}
