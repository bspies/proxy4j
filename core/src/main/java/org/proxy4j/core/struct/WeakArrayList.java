package org.proxy4j.core.struct;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * An array list of weak references.
 * @author Brennan Spies
 */
public class WeakArrayList<T> extends AbstractList<T>
{
    private List<WeakReference<T>> internalList;

    public WeakArrayList() {
        internalList = new ArrayList<WeakReference<T>>();
    }

    @Override public void add(int index, T element) {
        internalList.add(index, new WeakReference<T>(element));
    }

    @Override public boolean add(T o) {
        return internalList.add(new WeakReference<T>(o));
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
        return new Iterator<T>() {
            private Iterator<WeakReference<T>> internal = internalList.iterator();

            public boolean hasNext() {
                return internal.hasNext();
            }
            public T next() {
                WeakReference<T> n = internal.next();
                return n==null?null:n.get();
            }
            public void remove() {
                internal.remove();
            }
        };
    }
}
