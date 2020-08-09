package org.proxy4j.core.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache for classes.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class ClassCache
{
    private static final Map<Key, WeakReference<Class<?>>> classMap
        = new HashMap<>(); //TODO replace with ConcurrentHashMap?

    @SuppressWarnings("unchecked")
    public <T> Class<T> getClass(Key key) {
        WeakReference<Class<?>> ref;
        return ((ref = classMap.get(key))!=null) ? (Class<T>)ref.get() : null;
    }

    public void cache(Key key, Class<?> type) {
        classMap.put(key, new WeakReference<>(type));
    }
}
