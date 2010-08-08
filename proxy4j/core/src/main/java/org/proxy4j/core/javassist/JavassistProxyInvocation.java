package org.proxy4j.core.javassist;

import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * <p>A Javassist implementation of {@link ProxyInvocation}.</p>
 * @author Brennan Spies
 */
public class JavassistProxyInvocation<T> implements ProxyInvocation<T>
{
    private T proxy;
    private Method method;
    private Object[] args;

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
