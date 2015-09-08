package org.proxy4j.core.jdk;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * <p>JDK {@link java.lang.reflect.Proxy Proxy}-based implementation of
 * a {@code MethodInvocation}.</p>
 * @author Brennan Spies
 */
public class JdkMethodInvocation implements MethodInvocation
{
    private Object target;
    private Method method;
    private Object[] args;

    public JdkMethodInvocation(Object target, Method method, Object[] args) {
       this.target = target;
       this.method = method;
       this.args = args!=null ? args : new Object[0];
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
    public Object[] getArguments() {
        return args;
    }

    /**
     * {@inheritDoc}
     */    
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }

    /**
     * {@inheritDoc}
     */
    public Object getThis() {
        return target;
    }

    /**
     * {@inheritDoc}
     */
    public AccessibleObject getStaticPart() {
        return method;
    }
}
