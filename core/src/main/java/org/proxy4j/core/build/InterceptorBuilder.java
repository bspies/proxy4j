package org.proxy4j.core.build;

/**
 * The {@code InterceptorBuilder} is the root builder for an interceptor proxy, otherwise
 * known as a "protection" proxy or "Around Advice" in AOP. This class represents the start state
 * in the builder graph, which may go through one or more intermediate steps before creating the
 * final proxy.
 * The API for building an interceptor proxy is a simple "fluent interface" design:
 * <pre>Target proxy = proxyFactory.buildInterceptor(Target.class)
 *                  .on(myTarget)
 *                  .using(method1, interceptor1)
 *                  .using(method2, interceptor2, interceptor3)
 *                  .create(); </pre>
 * One or more intermediate <code>using()</code> methods will be called prior to creating the
 * final interceptor proxy.
 * @author Brennan Spies
 * @see org.proxy4j.core.ProxyFactory#buildInterceptor(Class)
 */
public interface InterceptorBuilder<T> {
    /**
     * Specifies the target of the method interceptor(s).
     * @param target The target
     * @return An intermediate builder for binding interceptors
     */
    public InterceptorBindingBuilder<T> on(T target);
}
