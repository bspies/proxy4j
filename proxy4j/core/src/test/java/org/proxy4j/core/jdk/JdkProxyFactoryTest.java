package org.proxy4j.core.jdk;

import org.proxy4j.core.BaseProxyFactoryTest;
import org.proxy4j.core.ProxyFactory;

/**
 * <p></p>
 * @author Brennan Spies
 */
public class JdkProxyFactoryTest extends BaseProxyFactoryTest {
    @Override protected ProxyFactory getImplementation() {
        return new JdkProxyFactory();
    }
}
