package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

/**
 * Generates proxy classes using CGLIB.
 * @author Brennan Spies
 * @since 1.0.0
 */
abstract class ClassGenerator<T>
{
    /**
     * Generates a proxy class for a single proxy class or interface.
     */
    static class SingleTypeGenerator<T> extends ClassGenerator<T>
    {
        private final ClassLoader loader;
        private final Class<T> proxyClass;

        private SingleTypeGenerator(ClassLoader loader, Class<T> proxyClass) {
           this.loader = loader;
           this.proxyClass = proxyClass;
        }

        @SuppressWarnings("unchecked")
        Class<T> generate(Class<? extends Callback> callbackClass) {
            Enhancer enhancer = newEnhancer(loader, proxyClass);
            enhancer.setCallbackType(callbackClass);
            return (Class<T>)enhancer.createClass();
        }

        @SuppressWarnings("unchecked")
        Class<T> generate(CallbackFilter filter, Class<? extends Callback>... callbackClasses) {
            Enhancer enhancer = newEnhancer(loader, proxyClass);
            enhancer.setCallbackTypes(callbackClasses);
            enhancer.setCallbackFilter(filter);
            return (Class<T>)enhancer.createClass();
        }
    }

    /**
     * Generates a proxy class for multiple proxy interfaces.
     */
    static class MultiTypeGenerator<T> extends ClassGenerator<T>
    {
        private final ClassLoader loader;
        private Class<T> superType;
        private final Class<?>[] proxyInterfaces;

        private MultiTypeGenerator(ClassLoader loader, Class<?>[] proxyInterfaces) {
            this.loader = loader;
            this.proxyInterfaces = proxyInterfaces;
            //TODO superType?
        }

        private MultiTypeGenerator(ClassLoader loader, Class<T> superType,  Class<?>[] proxyInterfaces) {
            this.loader = loader;
            this.proxyInterfaces = proxyInterfaces;
            this.superType = superType;
        }

        @SuppressWarnings("unchecked")
        Class<T> generate(Class<? extends Callback> callbackClass) {
            Enhancer enhancer = newEnhancer(loader, superType, proxyInterfaces);
            enhancer.setCallbackType(callbackClass);
            return (Class<T>)enhancer.createClass();
        }

        @SuppressWarnings("unchecked")
        Class<T> generate(CallbackFilter filter, Class<? extends Callback>... callbackClasses) {
            Enhancer enhancer = newEnhancer(loader, superType, proxyInterfaces);
            enhancer.setCallbackTypes(callbackClasses);
            enhancer.setCallbackFilter(filter);
            return (Class<T>)enhancer.createClass();
        }
    }

    /**
     * Returns a {@code ClassGenerator} for a single type.
     * @param loader The loader for the proxy class
     * @param type The proxy class
     * @param <T> The proxy type
     * @return The class generator
     */
    static <T> ClassGenerator<T> forType(ClassLoader loader, Class<T> type) {
       return new SingleTypeGenerator<>(loader, type);
    }

    /**
     * Returns a {@code ClassGenerator} for the given interface types.
     * @param loader The loader for the proxy class
     * @param interfaces The proxy interfaces
     * @return The class generator
     */
    static ClassGenerator<?> forTypes(ClassLoader loader, Class<?>... interfaces) {
       return new MultiTypeGenerator(loader, interfaces);
    }

    static <T> ClassGenerator<T> forTypes(ClassLoader loader, Class<T> superType, Class<?>... interfaces) {
        return new MultiTypeGenerator<>(loader, superType, interfaces);
    }

    /**
     * Generates the proxy class with the given {@code Callback} type.
     * @param callbackClass The callback type  
     * @return The proxy class
     */
    abstract Class<T> generate(Class<? extends Callback> callbackClass);

    /**
     * Generates the proxy class with the given {@code Callback} types and
     * filter.
     * @param filter The callback filter
     * @param callbackClasses The callback types
     * @return The proxy class
     */
    abstract Class<T> generate(CallbackFilter filter, Class<? extends Callback>... callbackClasses);

    /**
     * Creates a new {@link Enhancer} for the given type, which
     * also implements the given interfaces.
     * @param loader The class loader to use
     * @param type The type to enhance
     * @param interfaces The interfaces for the proxy to implement
     * @return The new enhancer
     */
    protected Enhancer newEnhancer(ClassLoader loader, Class<?> type, Class<?>[] interfaces) {
       Enhancer enhancer = newEnhancer(loader, type);
       enhancer.setInterfaces(interfaces);
       return enhancer;
    }

    /**
     * Creates a new {@link Enhancer} for the given type.
     * @param loader The class loader to use
     * @param type The type to enhance
     * @return The new enhancer
     */
    protected Enhancer newEnhancer(ClassLoader loader, Class<?> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setUseFactory(false);
        enhancer.setClassLoader(loader);
        enhancer.setSuperclass(type);
        return enhancer;
    }
}
