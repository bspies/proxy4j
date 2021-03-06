package org.proxy4j.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * An adaptation of Guice's <a href="http://code.google.com/p/google-guice/source/browse/trunk/src/com/google/inject/internal/InterceptorStackCallback.java">InterceptorStackCallback</a>
 * (author: Bob Lee), but generalized to use the AOP alliance interfaces. This class is used to hold a chain of
 * {@link MethodInterceptor MethodInterceptors}, which are invoked <i>in order</i> by the proxy. Any interceptor that
 * does not call {@code MethodInvocation#proceed()} will effectively short-circuit the traversal of the chain and
 * prevent the target from being called.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class InterceptorChain implements MethodInterceptor
{
    private final MethodInterceptor[] interceptors;

    public InterceptorChain(List<MethodInterceptor> interceptorList) {
       this.interceptors = interceptorList.toArray(new MethodInterceptor[interceptorList.size()]);
    }

    public InterceptorChain(MethodInterceptor... interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Invokes the interceptor chain.
     * @param methodInvocation The method invocation
     * @return The return value
     * @throws Throwable If an error occurs during invocation
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return new InterceptedMethodInvocation(methodInvocation).proceed();
    }

    private class InterceptedMethodInvocation implements MethodInvocation
    {
        MethodInvocation target;
        int index = -1;

        InterceptedMethodInvocation(MethodInvocation target) {
            this.target = target;
        }

        public Method getMethod() {
            return target.getMethod();
        }

        public Object[] getArguments() {
            return target.getArguments();
        }

        public Object proceed() throws Throwable {
            index++;
            try {
                return index==interceptors.length ? target.proceed() :
                    interceptors[index].invoke(this);
            } finally {
                index--;
            }
        }

        public Object getThis() {
            return target.getThis();
        }

        public AccessibleObject getStaticPart() {
            return target.getStaticPart();
        }
    }
}
