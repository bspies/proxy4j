package org.proxy4j.core.util;

import java.lang.ref.WeakReference;

/**
 * Base class for compound keys used in caching proxy types.
 * @author Brennan Spies
 * @since 1.0.0
*/
public abstract class Key
{
    private final WeakReference<ClassLoader> classLoaderRef;

    /**
     * Each {@code Key} implementation takes the class loader of
     * the proxy type as well as the classes which are being proxied.
     * @param classLoader The class loader of the proxy
     */
    public Key(ClassLoader classLoader) {
        this.classLoaderRef = new WeakReference<>(classLoader);
    }

    /**
     * Returns the class loader of the proxy.
     * @return The class loader
     */
    protected ClassLoader getClassLoader() {
        return classLoaderRef.get();
    }

    /**
     * Every {@code Key} subclass must define its own {@link Object#equals(Object)} method.
     */
    @Override public abstract boolean equals(Object o);

    /**
     * Every {@code Key} subclass must define its own {@link Object#hashCode()} method.
     * @return The hash code
     */
    @Override public abstract int hashCode();
}
