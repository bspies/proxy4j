package org.proxy4j.core.jdk;

import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * <p></p>
* @author Brennan Spies
*/
class JdkProxyInvocation<T> implements ProxyInvocation<T>
{
    private T proxy;
    private Method method;
    private Object[] args;

    JdkProxyInvocation(T proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
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
