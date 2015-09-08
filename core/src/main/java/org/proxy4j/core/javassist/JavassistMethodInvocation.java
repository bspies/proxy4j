package org.proxy4j.core.javassist;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * <p></p>
 * @author Brennan Spies
 */
public abstract class JavassistMethodInvocation<T> implements MethodInvocation
{
    private T target;
    private String methodName;
    private Object[] args;

    public JavassistMethodInvocation(T target, String methodName, Object... args) {
       this.target = target;
       this.methodName = methodName;
       this.args = args;
    }

    public Method getMethod() {
        Class[] argTypes = new Class[args.length];
        for(int i=0; i<args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        try {
            return target.getClass().getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Invalid MethodInvocation for: " + methodName, e);
        }
    }

    public Object[] getArguments() {
        return args;
    }

    public T getThis() {
        return target;
    }

    public AccessibleObject getStaticPart() {
        return getMethod();
    }
}
