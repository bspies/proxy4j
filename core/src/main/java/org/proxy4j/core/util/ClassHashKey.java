package org.proxy4j.core.util;

import org.proxy4j.core.struct.WeakHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * <p>{@link Key} implementation that uses the classes implmented or extended
 * by the proxy class.</p>
 * @author Brennan Spies
 */
public final class ClassHashKey extends Key
{
    private final Set<Class<?>> classes = new WeakHashSet<Class<?>>();
    private final int hash;
    
    /**
     * Constructor takes the class loader of the proxy and the classes
     * which the proxy extends or implements.
     * @param loader The classloader
     * @param classes The classes
     */
    public ClassHashKey(ClassLoader loader, Class<?>... classes) {
        this(loader, Arrays.asList(classes));
    }

    /**
     * Constructor takes the class loader of the proxy and the classes
     * which the proxy extends or implements.
     * @param loader The classloader
     * @param classes The classes
     */
    public ClassHashKey(ClassLoader loader, Collection<Class<?>> classes) {
        super(loader);
        if(classes.size()==0)
            throw new IllegalArgumentException("Must have at least one java.lang.Class to construct the Key");
        this.classes.addAll(classes);
        //cache the hash code value
        hash = internalHash();
    }

    private int internalHash() {
        int result = 17;
        result = 37*result + getClassLoader().hashCode();
        result = 37*result + classes.hashCode();
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassHashKey that = (ClassHashKey) o;
        //check for GC'd classloader
        return getClassLoader()!=null &&
            getClassLoader().equals(that.getClassLoader()) &&
            classes.equals(that.classes);
    }

    @Override public int hashCode() {
        return hash;
    }
}
