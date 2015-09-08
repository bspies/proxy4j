package org.proxy4j.core.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.proxy4j.core.cglib.CglibProxyFactoryTest;
import org.proxy4j.core.javassist.JavassistProxyFactoryTest;

/**
 * @author Brennan Spies
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CglibProxyFactoryTest.class,
        JavassistProxyFactoryTest.class,
        CglibProxyFactoryTest.class
})
public class ProxyImplementationsSuite {
}
