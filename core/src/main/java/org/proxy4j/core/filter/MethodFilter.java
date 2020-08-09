package org.proxy4j.core.filter;

import java.lang.reflect.Method;

/**
 * Filter used by proxy creators to select which methods should be
 * intercepted.
 * @author Brennan Spies
 * @since 1.0.0
 */
public interface MethodFilter {
    /**
     * Returns true if the given method should be intercepted.
     * @param method The method to test
     * @return True if the argument method should be intercepted
     */
    public boolean accept(Method method);
}
