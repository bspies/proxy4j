package org.proxy4j.core.javassist;

import org.proxy4j.core.BaseProxyFactory;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.ProxyHandler;
import org.proxy4j.core.ProxyLoader;
import org.proxy4j.core.build.InterceptorBuilder;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * {@link org.proxy4j.core.ProxyFactory ProxyFactory} implementation using
 * <a href="http://www.csg.is.titech.ac.jp/~chiba/javassist/">Javassist</a>.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class JavassistProxyFactory extends BaseProxyFactory
{
    private final ClassGenerator classGenerator;

    public JavassistProxyFactory() {
        super();
        classGenerator = new ClassGenerator(getNamingStrategy());
    }

    @Inject
    public JavassistProxyFactory(@ProxyLoader ClassLoader loader) {
        super(loader);
        classGenerator = new ClassGenerator(getNamingStrategy());
        //initializes class pool for preferred loader
        classGenerator.getClassPool(loader);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T createProxy(Class<T> proxyType, Provider<T> provider) throws GenerationException {
        try {
            Class<T> proxyClass = classGenerator.getProviderProxyClass(getProxyClassLoader(proxyType), proxyType);
            return proxyClass.getConstructor(Provider.class).newInstance(provider);
        } catch (Exception e) {
            throw new GenerationException("Error creating Provider proxy", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> T createProxy(Class<T> proxyType, ProxyHandler<T> handler) throws GenerationException {
        try {
            Class<T> proxyClass = classGenerator.getHandlerProxyClass(getProxyClassLoader(proxyType), proxyType);
            return proxyClass.getConstructor(ProxyHandler.class).newInstance(handler);
        } catch (Exception e) {
            throw new GenerationException("Error creating ProxyHandler proxy", e);
        }
    }

    /**
     *{@inheritDoc}
     */
    public Object createProxy(Class<?>[] proxyInterfaces, ProxyHandler<?> handler) throws GenerationException {
        assertInterfaces(proxyInterfaces);
        try {
            Class<?> proxyClass = classGenerator.getHandlerProxyClass(getProxyClassLoader(proxyInterfaces[0]), proxyInterfaces);
            return proxyClass.getConstructor(ProxyHandler.class).newInstance(handler);
        } catch (Exception e) {
            throw new GenerationException("Error creating ProxyHandler proxy", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> InterceptorBuilder<T> buildInterceptor(Class<T> proxyClass) {
        return new JavassistInterceptorBuilder<>(getProxyClassLoader(proxyClass),
                proxyClass, classGenerator);
    }
}
