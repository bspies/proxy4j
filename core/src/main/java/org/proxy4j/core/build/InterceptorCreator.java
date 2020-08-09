package org.proxy4j.core.build;

import org.proxy4j.core.GenerationException;

/**
 * The final builder used internally by {@link InterceptorBuilder}
 * to build the proxy when the minimal required state has been reached.
 * @author Brennan Spies
 * @since 1.0.0
 */
public interface InterceptorCreator<T> {
    /**
     * Creates the interceptor proxy using information accumulated in
     * previous build steps.
     * @return The interceptor proxy
     * @throws GenerationException If an error occurs generating the proxy
     */
    T create() throws GenerationException;
}
