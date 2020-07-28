package org.proxy4j.core.build;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * Binds a method to a chain of interceptors.
 * @author Brennan Spies
 */
public interface MethodBinder<T> {
    /**
     * Binds the given method to a chain of interceptors.
     * @param method The method to bind
     * @param interceptors The chain of interceptors
     * @return A builder for binding further methods
     */
    public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors);
}
