package org.proxy4j.core.util;

/**
 * The naming policy for the proxy class.
 * @author Brennan Spies
 * @since 1.0.0
 */
public interface NamingPolicy {
    /**
     * Returns the generated proxy class name based on the given type
     * and the key.
     * @param baseClassName The fully-qualified name of the proxy supertype (null if none given)
     * @param key The lookup key for the proxy class
     * @return The name of the proxy class
     */
    String getProxyName(String baseClassName, Object key);
}
