package org.proxy4j.core.build;

import org.aopalliance.intercept.MethodInterceptor;
import org.proxy4j.core.InterceptorFactory;
import org.proxy4j.core.filter.MethodFilter;

import java.lang.annotation.Annotation;

/**
 * An intermediate builder used by {@link InterceptorBuilder} to bind a set of
 * methods on the target type to the given interceptors. Clients should not refer to
 * this class directly but instead use the fluent API documented {@link InterceptorBuilder here}.
 * @author Brennan Spies
 * @since 1.0
 */
public interface InterceptorBindingBuilder<T> extends MethodBinder<T> {
    /**
     * Uses the given method filter to selectively bind methods on the class of type {@code T} to
     * the given chain of interceptors. The interceptors are shared between all proxied methods.
     * @param filter The method filter
     * @param interceptors The interceptor chain
     * @return The final builder, used to create proxy
     */
    public InterceptorCreator<T> using(MethodFilter filter, MethodInterceptor... interceptors);

    /**
     * Uses the given method filter to selectively bind methods on the class of type {@code T} to
     * a chain of interceptors produced by the given factory. Hence the interceptors are not (necessarily)
     * shared between all proxied methods.
     * @param filter The method filter
     * @param factory The interceptor factory
     * @return The final builder, used to create the proxy
     */
    public InterceptorCreator<T> using(MethodFilter filter, InterceptorFactory factory);

    /**
     * Uses the annotation to selectively bind methods on the class of type {@code T} to the given
     * chain of interceptors. The interceptors are shared between all proxied methods.
     * @param methodMarker The annotation
     * @param interceptors The interceptor chain
     * @return The final builder, used to create the proxy
     */
    public InterceptorCreator<T> using(Class<? extends Annotation> methodMarker, MethodInterceptor... interceptors);
}
