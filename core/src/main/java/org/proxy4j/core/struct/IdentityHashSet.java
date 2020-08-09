package org.proxy4j.core.struct;

import java.util.*;

/**
 * A {@link Set} implementation based on reference (not object) equality. This
 * is an intentional violation of the {@link Set#equals(Object)} contract for specialized
 * purposes. Uses a backing instance of {@link java.util.IdentityHashMap}.
 * @author Brennan Spies
 */
public class IdentityHashSet<T> extends AbstractSet<T> implements Set<T>
{
    private final IdentityHashMap<T,Object> idmap;
    private static final Object NULL_OBJ = new Object();

    public IdentityHashSet() {
       idmap = new IdentityHashMap<>();
    }

    public IdentityHashSet(int expectedMaxSize) {
       idmap = new IdentityHashMap<>(expectedMaxSize);
    }

    public IdentityHashSet(Collection<? extends T> c) {
       this(c.size());
       addAll(c);
    }

    /**
     * @see Set#add(Object)
     */
    @Override public boolean add(T o) {
        return idmap.put(o, NULL_OBJ)==null;
    }

    /**
     * @see java.util.Set#clear()
     */
    @Override public void clear() {
        idmap.clear();
    }

    /**
     * @see java.util.Set#isEmpty()
     */
    @Override public boolean isEmpty() {
        return idmap.isEmpty();
    }

    /**
     * @see java.util.Set#iterator() 
     */
    @Override public Iterator<T> iterator() {
        return idmap.keySet().iterator();
    }

    /**
     * @see Set#remove(Object) 
     */
    @Override public boolean remove(Object o) {
        return idmap.remove(o)==NULL_OBJ;
    }

    /**
     * @see Set#removeAll(java.util.Collection)
     */
    @Override public boolean removeAll(Collection<?> c) {
        return idmap.keySet().removeAll(c);
    }

    /**
     * @see java.util.Set#size()
     */
    @Override public int size() {
        return idmap.size();
    }
}
