package org.proxy4j.core.javassist;

import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * A Javassist implementation of {@link ProxyInvocation}.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class JavassistProxyInvocation<T> implements ProxyInvocation<T>
{
    private final T proxy;
    private final Method method;
    private final Object[] args;

    public JavassistProxyInvocation(T proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(T target) throws Throwable {
        return method.invoke(target, args);
    }

    /**
     * {@inheritDoc}
     */
    public Method getMethod() {
        return method;
    }

    /**
     * {@inheritDoc}
     */
    public T getProxy() {
        return proxy;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getArguments() {
        return args;
    }
}
