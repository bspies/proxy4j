package org.proxy4j.core.cglib;

import org.proxy4j.core.BaseProxyFactoryTest;
import org.proxy4j.core.ProxyFactory;

/**
 * <p>Unit tests for {@link CglibProxyFactory}.</p>
 * @author Brennan Spies
 */
public class CglibProxyFactoryTest extends BaseProxyFactoryTest
{
    @Override
    protected ProxyFactory getImplementation() {
        return new CglibProxyFactory();
    }
}
