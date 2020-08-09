package org.proxy4j.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.proxy4j.core.util.DefaultNamingPolicy;
import org.proxy4j.core.util.NamingPolicy;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;

/**
 * <p>Abstract base implementation of {@link ProxyFactory}.</p>
 * @author Brennan Spies
 * @since 1.0.0
 */
public abstract class BaseProxyFactory implements ProxyFactory
{
    private WeakReference<ClassLoader> preferredClassLoader;
    private NamingPolicy namingPolicy = new DefaultNamingPolicy();

    /**
     * Default constructor with no preferred class loader.
     */
    public BaseProxyFactory() {}

    /**
     * Constructor that takes the preferred class loader for loading
     * proxy interfaces. 
     * @param preferredClassLoader The class loader for proxy interfaces
     */
    public BaseProxyFactory(ClassLoader preferredClassLoader) {
        this.preferredClassLoader = new WeakReference<>(preferredClassLoader);
    }

    /**
     * Returns a {@link org.proxy4j.core.util.NamingPolicy} that can be used to create
     * proxy class names.
     * @return The naming strategy
     */
    protected NamingPolicy getNamingStrategy() {
        return namingPolicy;
    }

    /**
     * Sets the naming policy used to determine the proxy class name.
     * @param namingPolicy The naming policy to set
     */
    @Inject
    public void setNamingStrategy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
    }

    /**
     * Returns the specified {@link ClassLoader} that is used to define any
     * proxy interfaces.
     * @return The {@code ClassLoader} used to define proxy interfaces 
     */
    protected ClassLoader getPreferredClassLoader() {
        return preferredClassLoader==null ? null : preferredClassLoader.get();
    }

    /**
     * Returns the {@link ClassLoader} that should be used to load
     * the proxy.
     * @param proxyType The class of the proxy
     * @return The {@code ClassLoader}
     */
    protected ClassLoader getProxyClassLoader(Class<?> proxyType) {
        if(getPreferredClassLoader() != null)
            return getPreferredClassLoader();
        return proxyType.getClassLoader();
    }

    /**
     * Verifies that the given class is visible from the given class loader.
     * @param loader The class loader that needs visibility to the class
     * @param type The class to verity
     * @throws GenerationException If {@code type} is not visible from the class loader
     */
    protected void assertClassVisible(ClassLoader loader, Class<?> type) throws GenerationException {
        Class<?> loadedType = null;
        try {
          loadedType = Class.forName(type.getName(), false, loader);
        } catch (ClassNotFoundException e) {}
        if(loadedType!=type)
            throw new GenerationException(type + " is not visible from class loader");
    }

    /**
     * Verifies that all the given classes are interfaces.
     * @param types The classes to test
     * @throws GenerationException If at least one class is not an interface
     */
    protected void assertInterfaces(Class<?>... types) throws GenerationException {
        for(Class<?> t : types) {
            if(!t.isInterface())
                throw new GenerationException("Class " + t.getCanonicalName() + " is not an interface");
        }
    }

    /**
     * @see ProxyFactory#createProxy(Class, Object, Class, org.aopalliance.intercept.MethodInterceptor...)
     */
    public <T> T createProxy(Class<T> proxyClass, T target, final Class<? extends Annotation> marker, final MethodInterceptor... interceptors) throws GenerationException {
        if(interceptors.length==0)
            throw new IllegalArgumentException("Must be at least 1 MethodInterceptor");
        return buildInterceptor(proxyClass)
                .on(target)
                .using(marker, interceptors)
                .create();
    }
}
