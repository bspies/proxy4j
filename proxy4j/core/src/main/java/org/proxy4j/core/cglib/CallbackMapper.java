package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.Callback;
import java.lang.reflect.Method;

/**
 * <p>Interface used by {@link ProxyCreator} to map methods to callbacks.</p>
 * @author Brennan Spies
 */
interface CallbackMapper {
    /**
     * Maps a class method to its corresponding proxy {@link Callback}.
     * @param method The method to map
     * @return The corresponding callback
     */
    Callback map(Method method);
}
