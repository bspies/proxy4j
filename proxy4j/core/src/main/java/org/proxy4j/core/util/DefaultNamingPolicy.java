package org.proxy4j.core.util;

/**
 * <p>A default implementation of {@link NamingPolicy}.</p>
 * @author Brennan Spies
 */
public class DefaultNamingPolicy implements NamingPolicy
{
    public String getProxyName(String baseClassName, Object key) {
        return baseClassName + "_Proxy$$" + Integer.toHexString(key.hashCode());
    }
}
