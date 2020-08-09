package org.proxy4j.core.jdk;

import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * JDK proxy implementation of the proxy invocation.
 * @author Brennan Spies
 * @since 1.0.0
*/
class JdkProxyInvocation<T> implements ProxyInvocation<T>
{
    private final T proxy;
    private final Method method;
    private final Object[] args;

    JdkProxyInvocation(T proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.args = args!=null ? args : new Object[0];
    }

    public Object invoke(Object target) throws Throwable {
        return method.invoke(target, args);
    }

    public Method getMethod() {
        return method;
    }

    public T getProxy() {
        return proxy;
    }

    public Object[] getArguments() {
        return args;
    }
}
