package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.MethodProxy;
import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * A CGLIB implementation of {@link org.proxy4j.core.ProxyInvocation}.
 * @since 1.0.0
 */
class CglibProxyInvocation<T> implements ProxyInvocation<T>
{
    private final T proxy;
    private final Method method;
    private final MethodProxy methodProxy;
    private final Object[] args;

    CglibProxyInvocation(T proxy, Method method, MethodProxy methodProxy, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.methodProxy = methodProxy;
        this.args = args;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(T target) throws Throwable {
        return methodProxy.invoke(target, args);
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
