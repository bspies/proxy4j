package org.proxy4j.core.util;

/**
 * <p></p>
 * @author Brennan Spies
 */
public interface NamingPolicy {
    /**
     * Returns the generated proxy class name based on the given type
     * and the key.
     * @param baseClassName The fully-qualified name of the proxy supertype (null if none given)
     * @param key The lookup key for the proxy class
     * @return The name of the proxy class
     */
    public String getProxyName(String baseClassName, Object key);
}
