package org.proxy4j.core.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.proxy4j.core.cglib.CglibProxyFactoryTest;
import org.proxy4j.core.javassist.JavassistProxyFactoryTest;
import org.proxy4j.core.jdk.JdkProxyFactoryTest;

/**
 * @author Brennan Spies
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CglibProxyFactoryTest.class,
        JavassistProxyFactoryTest.class,
        JdkProxyFactoryTest.class
})
public class ProxyImplementationsSuite {
}
