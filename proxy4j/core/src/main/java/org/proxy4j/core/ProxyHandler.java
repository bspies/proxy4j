package org.proxy4j.core;

/**
 *  <p>A method invocation handler for proxy invocations.</p>
 * @author Brennan Spies
 * @since 1.0
 */
public interface ProxyHandler<T> {
    /**
     * Handles the method invocation on the proxy.
     * @param invocation The invocation on the proxy
     * @return The return value of the invocation
     * @throws Throwable If an error is thrown from the invocation
     */
    public Object handle(ProxyInvocation<T> invocation) throws Throwable;
}
