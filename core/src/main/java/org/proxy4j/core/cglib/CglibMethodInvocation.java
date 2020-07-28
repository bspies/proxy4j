package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * CGLIB-based implementation of {@link MethodInvocation}
 * @author Brennan Spies
 */
class CglibMethodInvocation implements MethodInvocation
{
    private final Object target;
    private final Method method;
    private final MethodProxy methodProxy;
    private final Object[] args;

    CglibMethodInvocation(Object target, Method method, MethodProxy methodProxy, Object[] args) {
        this.target = target;
        this.method = method;
        this.methodProxy = methodProxy;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return args;
    }

    public Object proceed() throws Throwable {
        return methodProxy.invoke(target, args);
    }

    public Object getThis() {
        return target;
    }

    public AccessibleObject getStaticPart() {
        return method;
    }
}
