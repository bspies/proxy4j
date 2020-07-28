package org.proxy4j.core;

import java.lang.reflect.Method;

/**
 * Describes a method invocation on a proxy.
 * @author Brennan Spies
 * @since 1.0
 */
public interface ProxyInvocation<T>
{
    /**
     * Executes this invocation on a target object of the same type.
     * @param target The target
     * @return The return value of the invocation
     * @throws Throwable If an error is thrown by the invocation
     */
    public Object invoke(T target) throws Throwable;

    /**
     * Return the method on which the proxy invocation was made.
     * @return The method
     */
    public Method getMethod();

    /**
     * Returns the proxy on which the method was invoked.
     * @return The proxy
     */
    public T getProxy();

    /**
     * The arguments passed to the target proxy invocation.
     * @return The arguments
     */
    public Object[] getArguments();
}
