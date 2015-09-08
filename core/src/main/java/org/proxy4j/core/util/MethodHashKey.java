package org.proxy4j.core.util;

import org.proxy4j.core.struct.WeakHashSet;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * <p></p>
 * @author Brennan Spies
 */
public final class MethodHashKey extends Key
{
    private final Set<Method> methods = new WeakHashSet<Method>();
    private final int hash;

    /**
     * {@link Key} subclass where individual proxied methods are used to
     * lookup the proxy class.
     * @param loader The class loader
     * @param methods The proxied methods
     */
    public MethodHashKey(ClassLoader loader, Method[] methods) {
        this(loader, Arrays.asList(methods));
    }

    public MethodHashKey(ClassLoader loader, Collection<Method> methods) {
        super(loader);
        this.methods.addAll(methods);
        hash = internalHash();
    }

    private int internalHash() {
        int result = 17;
        if(getClassLoader()!=null)
            result = 37*result + getClassLoader().hashCode();
        result = 37*result + methods.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodHashKey that = (MethodHashKey) o;
        return getClassLoader()!=null &&
            getClassLoader().equals(that.getClassLoader()) &&
            methods.equals(that.methods);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
