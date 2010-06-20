package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.MethodProxy;
import org.proxy4j.core.ProxyInvocation;

import java.lang.reflect.Method;

/**
 * A CGLIB implementation of {@link org.proxy4j.core.ProxyInvocation}.
 */
class CglibProxyInvocation<T> implements ProxyInvocation<T>
{
    T proxy;
    Method method;
    MethodProxy methodProxy;
    private Object[] args;

    CglibProxyInvocation(T proxy, Method method, MethodProxy methodProxy, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.methodProxy = methodProxy;
        this.args = args;
    }

    public Object invoke(T target) throws Throwable {
        return methodProxy.invoke(target, args);
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
