package org.proxy4j.core.util;

/**
 * A default implementation of {@link NamingPolicy}.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class DefaultNamingPolicy implements NamingPolicy
{
    public String getProxyName(String baseClassName, Object key) {
        return baseClassName + "_Proxy$$" + Integer.toHexString(key.hashCode());
    }
}
