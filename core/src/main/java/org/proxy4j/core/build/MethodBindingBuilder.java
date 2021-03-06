package org.proxy4j.core.build;

/**
 * An intermediate builder used internally by {@link InterceptorBuilder}
 * to bind methods to interceptors. Clients should generally not refer to this class
 * directly but instead use the fluent API documented {@link InterceptorBuilder here}.
 * @author Brennan Spies
 * @since 1.0.0
 */
public interface MethodBindingBuilder<T> extends MethodBinder<T>, InterceptorCreator<T> {
}
