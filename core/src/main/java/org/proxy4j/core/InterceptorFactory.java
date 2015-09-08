package org.proxy4j.core;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>Factory which can be used to create per-method instances of interceptor(s).</p>
 * @author Brennan Spies
 * @since 1.0
 */
public interface InterceptorFactory {
    /**
     * Returns a list of interceptors for the given method.
     * @param method The method to be intercepted
     * @return The interceptor chain (in order of invocation)
     */
    public List<MethodInterceptor> getInterceptors(Method method);
}
