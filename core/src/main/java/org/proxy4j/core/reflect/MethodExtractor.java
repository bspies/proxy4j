package org.proxy4j.core.reflect;

import org.proxy4j.core.filter.MethodFilter;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Interface for any class that can extract methods from a class for the purpose
 * of proxying them.
 * @author Brennan Spies
 */
public interface MethodExtractor {
    /**
     * Returns all methods that can be proxied. This excludes
     * methods that are {@code private}, {@code static}, or
     * {@code final}.
     * @return All methods that can be proxied
     */
    Collection<Method> getProxyableMethods();

    /**
     * Returns all {@code public} methods that are proxyable.
     * @return All public methods
     */
    Collection<Method> getPublicMethods();

    /**
     * Returns all methods from {@link #getProxyableMethods()},
     * filered by the given {@code MethodFilter}.
     * @param filter The method filter
     * @return All the non-private filtered methods
     */
    Collection<Method> getMethods(MethodFilter filter);

    /**
     * True if the {@code MethodExtractor} extracts {@code Object} methods.
     * @return True if {@code Object} methods extracted
     */
    public boolean isIncludeObjectMethods();
}
