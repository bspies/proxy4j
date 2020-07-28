package org.proxy4j.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.proxy4j.core.build.InterceptorBuilder;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * A factory interface for creating different types of proxies.
 * @author Brennan Spies
 * @since 1.0
 */
public interface ProxyFactory {
    /**
     * Creates a "virtual" proxy where method invocations on the proxy are delegated
     * to the provided instance.
     * @param proxyClass The proxied class
     * @param provider The provider of the proxied object
     * @param <T> The proxy type
     * @return The proxy
     * @throws GenerationException If an error occurs creating the proxy
     */
   public <T> T createProxy(Class<T> proxyClass, Provider<T> provider) throws GenerationException;

    /**
     * Creates a proxy where method invocations on the proxy are passed to a single
     * {@code invoke} method on the {@link ProxyHandler}.
     * @param proxyClass The proxied interface class
     * @param handler The handler for the method invocation
     * @param <T> The proxy type
     * @return The proxy
     * @throws GenerationException If an error occurs creating the proxy
     */
    public <T> T createProxy(Class<T> proxyClass, ProxyHandler<T> handler) throws GenerationException;

    /**
     * Creates a proxy that implements multiple interfaces where invocations on that proxy are
     * passed to a single {@code invoke} method on the {@link ProxyHandler}. Equivalent to
     * the traditional JDK proxy creation method:
     * {@link java.lang.reflect.Proxy#newProxyInstance(ClassLoader, Class[], java.lang.reflect.InvocationHandler)}.
     * @param proxyInterfaces The interfaces that the proxy class must implement
     * @param handler The handler for the method invocation
     * @return The proxy
     * @throws GenerationException If an error occurs creating the proxy
     */
    public Object createProxy(Class<?>[] proxyInterfaces, ProxyHandler<?> handler) throws GenerationException;

    /**
     * Creates a "protection" proxy where method invocations on the proxy are intercepted by the
     * chain of {@link MethodInterceptor MethodInterceptors} before (optionally) being invoked on the target.
     * @param proxyClass The proxy class
     * @param target The target of the invocation
     * @param marker The annotation that marks intercepted methods
     * @param interceptors One or more method interceptors, which are called in order
     * @param <T> The proxy type
     * @return The proxy
     * @throws GenerationException If an error occurs creating the proxy
     */
    public <T> T createProxy(Class<T> proxyClass, T target, Class<? extends Annotation> marker, MethodInterceptor... interceptors) throws GenerationException;

    /**
     * Creates a new builder for constructing an interceptor or "protection" proxy. The builder
     * allows fine-grained customization of how the target's methods are intercepted.
     * @param proxyClass The class of the proxy (and target)
     * @param <T> The proxy type
     * @return The builder
     */
    public <T> InterceptorBuilder<T> buildInterceptor(Class<T> proxyClass);
}
