package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import net.sf.cglib.reflect.FastClass;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.struct.IdentityHashSet;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Creates method-level proxies using a {@link CallbackMapper}.
 * @author Brennan Spies
 * @since 1.0.0
 */
class ProxyCreator<T>
{
    private final ClassGenerator<T> generator;
    private final Collection<Method> methods;

    ProxyCreator(ClassLoader loader, Class<T> proxyClass) {
        generator = ClassGenerator.forType(loader, proxyClass);
        methods = getMethods(proxyClass, null);
    }

    ProxyCreator(ClassLoader loader, Class<T> proxyClass, Class<?>[] proxyInterfaces) {
        generator = ClassGenerator.forTypes(loader, proxyClass, proxyInterfaces);
        methods = getMethods(proxyClass, proxyInterfaces);
    }

    public Collection<Method> getProxyableMethods() {
        return methods;
    }

    /**
     * Takes extracted methods and maps them to callbacks.
     * @param mapper The callback mapper
     * @return A map of methods to callbacks
     */
    private Map<Method, Callback> getCallbackMap(CallbackMapper mapper) {
        Map<Method,Callback> callbackMap = new HashMap<>();
        for(Method m : methods) {
           callbackMap.put(m, mapper.map(m));
        }
        for(Method m : Object.class.getDeclaredMethods()) {
           if(!callbackMap.containsKey(m))
            callbackMap.put(m, NoOp.INSTANCE);
        }
        return callbackMap;
    }

    private Callback[] getCallbacks(Map<Method,Callback> callbackMap) {
       Set<Callback> callbackSet = new IdentityHashSet<>(callbackMap.values());
       return callbackSet.toArray(new Callback[callbackSet.size()]);
    }

    //TODO replace with BasicMethodExtractor
    private Collection<Method> getMethods(Class<?> superType, Class<?>[] interfaces) {
        ArrayList<Method> mlist = new ArrayList<>();
        Enhancer.getMethods(superType, interfaces, mlist);
        return mlist;
    }

    /**
     * Creates a new proxy using the given {@code MethodMapper}.
     * @param mapper The callback mapper
     * @return The proxy
     * @throws GenerationException If an error occurs generating the proxy
     */
    T newProxy(CallbackMapper mapper) throws GenerationException
    {
        Map<Method, Callback> callbackMap = getCallbackMap(mapper);
        Callback[] callbacks = getCallbacks(callbackMap);
        @SuppressWarnings("unchecked")
        Class<? extends Callback>[] callbackClasses = new Class[callbacks.length];
        for(int i=0; i<callbackClasses.length; i++) {
            callbackClasses[i] = callbacks[i].getClass();
        }
        Class<T> proxyClass = generator.generate(new IndexingCallbackFilter(callbackMap, callbacks), callbackClasses);
        //registers callbacks, creates proxy, de-registers callbacks
        Enhancer.registerCallbacks(proxyClass, callbacks);
        FastClass fc = FastClass.create(proxyClass);
        try {
            return proxyClass.cast(fc.newInstance());
        } catch(Exception e) {
            throw new GenerationException("Error generating proxy instance", e);
        } finally {
            Enhancer.registerCallbacks(proxyClass, null);
        }
    }

    /**
     * {@code CallbackFilter} that maps methods to indexes.
     */
    private static class IndexingCallbackFilter implements CallbackFilter
    {
        private final Map<Method, Integer> index = new HashMap<Method, Integer>();

        IndexingCallbackFilter(Map<Method,Callback> map, Callback[] callbacks) {
            for (Map.Entry<Method, Callback> e : map.entrySet()) {
                int i = indexOf(e.getValue(), callbacks);
                if (i < 0) throw new IllegalArgumentException("Unmapped method: " + e.getKey());
                index.put(e.getKey(), i);
            }
        }

        private int indexOf(Callback callback, Callback[] callbacks) {
            for(int i=0; i<callbacks.length; i++) {
                if(callbacks[i]==callback) return i;
            }
            return -1;
        }

        public int accept(Method method) {
            return index.get(method);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IndexingCallbackFilter that = (IndexingCallbackFilter) o;
            return index.equals(that.index);
        }

        @Override
        public int hashCode() {
            return index.hashCode();
        }
    }
}
