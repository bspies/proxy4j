package org.proxy4j.core.struct;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author Brennan Spies
 * A {@link Set} implementation with weak references to contained elements.
 */
public class WeakHashSet<T> extends AbstractSet<T>
{
    private final ReferenceQueue<T> queue = new ReferenceQueue<T>();
    private final Set<WeakElement<T>> wrset;

    public WeakHashSet() {
        wrset = new HashSet<WeakElement<T>>();
    }

    public WeakHashSet(int capacity, float loadFactor) {
        wrset = new HashSet<WeakElement<T>>(capacity, loadFactor); 
    }

    @Override public boolean add(T o) {
        clearInvalidEntries();
        return wrset.add(createWeakElement(o));
    }

    @Override public boolean addAll(Collection<? extends T> c) {
        clearInvalidEntries();
        boolean b = true;
        for(T t : c) {
          b &= wrset.add(createWeakElement(t));
        }
        return b;
    }

    @Override public void clear() {
        wrset.clear();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean contains(Object o) {
        return wrset.contains(getTempWeakElement((T)o));
    }

    @Override public boolean isEmpty() {
        return wrset.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean remove(Object o) {
        boolean r = wrset.remove(getTempWeakElement((T)o));
        clearInvalidEntries();
        return r;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean removeAll(Collection<?> c) {
        boolean b = true;
        for(Object e : c) {
            b &= wrset.remove(getTempWeakElement((T)e));
        }
        clearInvalidEntries();
        return b;
    }

    @Override public Iterator<T> iterator() {
        final Iterator<WeakElement<T>> internal = wrset.iterator();
        return new Iterator<T>() {
            public boolean hasNext() { return internal.hasNext(); }
            public T next() { return getReferent(internal.next()); }
            public void remove() { internal.remove(); }
        };
    }

    @Override public int size() {
        return wrset.size();
    }

    //creates a new WeakElement and registers it with queue.
    private WeakElement<T> createWeakElement(T elem) {
        return new WeakElement<T>(elem, queue);
    }

    //constructs a temporary WeakElement for checking against Set values.
    private WeakElement<T> getTempWeakElement(T elem) {
        return new WeakElement<T>(elem);
    }

    private void clearInvalidEntries() {
        Object o;
        while ((o = queue.poll()) != null) {
            remove(o);
        }
    }

    private T getReferent(WeakElement<T> we) {
        return we==null ? null : we.get();
    }

    /**
     * Extension of {@link WeakReference} that caches the hash code
     * and redefines {@link WeakReference#equals(Object)}.
     */
    private static class WeakElement<T> extends WeakReference<T>
    {
        private final int hashCode;

        public WeakElement(T referent) {
            super(referent);
            hashCode = referent.hashCode();
        }

        public WeakElement(T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
            hashCode = referent.hashCode();
        }

        @Override public int hashCode() {
            return hashCode;
        }

        @Override public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof WeakElement))
                return false;
            Object t = this.get();
            Object u = ((WeakElement) obj).get();
            return t == u || !((t == null) || (u == null)) && t.equals(u);
        }
    }
}
