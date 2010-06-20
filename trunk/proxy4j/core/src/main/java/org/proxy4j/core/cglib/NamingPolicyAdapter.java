package org.proxy4j.core.cglib;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * <p>Adapts a {@link org.proxy4j.core.util.NamingPolicy NamingPolicy} for use
 * by CGLIB.</p>
 * @author Brennan Spies
 */
public class NamingPolicyAdapter implements NamingPolicy
{
    private org.proxy4j.core.util.NamingPolicy policy;

    public NamingPolicyAdapter(org.proxy4j.core.util.NamingPolicy policy) {
       this.policy = policy; 
    }

    /**
     * @see net.sf.cglib.core.NamingPolicy#getClassName(String, String, Object, net.sf.cglib.core.Predicate)
     */
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        return policy.getProxyName(prefix, key);
    }
}
