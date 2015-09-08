package org.proxy4j.core.javassist;

import org.proxy4j.core.BaseProxyFactoryTest;
import org.proxy4j.core.ProxyFactory;
import org.proxy4j.core.util.DefaultNamingPolicy;

/**
 * @author Brennan Spies
 * <p></p>
 */
public class JavassistProxyFactoryTest extends BaseProxyFactoryTest {
    @Override
    protected ProxyFactory getImplementation() {
        JavassistProxyFactory factory = new JavassistProxyFactory();
        factory.setNamingStrategy(new DefaultNamingPolicy());
        return factory;
    }
}
