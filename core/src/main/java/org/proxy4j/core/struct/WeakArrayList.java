package org.proxy4j.core.struct;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * An array list of weak references.
 * @author Brennan Spies
 * @since 1.0.0
 */
public class WeakArrayList<T> extends AbstractList<T>
{
    private final List<WeakReference<T>> internalList;

    public WeakArrayList() {
        internalList = new ArrayList<>();
    }

    @Override public void add(int index, T element) {
        internalList.add(index, new WeakReference<>(element));
    }

    @Override public boolean add(T o) {
        return internalList.add(new WeakReference<>(o));
    }

    @Override public T get(int index) {
        WeakReference<T> element = internalList.get(index);
        return element==null?null:element.get();
    }

    @Override public boolean addAll(Collection<? extends T> c) {
        boolean added = true;
        for(T element : c) {
          added &= add(element);
        }
        return added;
    }

    @Override public int size() {
        return internalList.size();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<>() {
            private final Iterator<WeakReference<T>> internal = internalList.iterator();

            public boolean hasNext() {
                return internal.hasNext();
            }

            public T next() {
                WeakReference<T> n = internal.next();
                return n == null ? null : n.get();
            }

            public void remove() {
                internal.remove();
            }
        };
    }
}
