package org.proxy4j.core.filter;

import java.lang.reflect.Method;

/**
 * <p>Filter used by proxy creators to select which methods should be
 * intercepted.</p>
 * @author Brennan Spies
 */
public interface MethodFilter {
    /**
     * Returns true if the given method should be intercepted.
     * @param method The method to test
     * @return True if the argument method should be intercepted
     */
    public boolean accept(Method method);
}
