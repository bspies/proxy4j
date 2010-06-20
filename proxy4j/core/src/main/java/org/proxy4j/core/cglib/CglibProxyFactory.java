package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.reflect.FastClass;
import org.proxy4j.core.BaseProxyFactory;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.ProxyHandler;
import org.proxy4j.core.build.InterceptorBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>{@link org.proxy4j.core.ProxyFactory ProxyFactory} implementation using <a href="http://cglib.sourceforge.net/">CGLIB</a>.
 * </p>
 * @author Brennan Spies
 * @since 1.0
 */
public class CglibProxyFactory extends BaseProxyFactory
{
    public CglibProxyFactory() {
        super();
    }

    @Inject
    public CglibProxyFactory(ClassLoader loader) {
        super(loader);
    }

    /**
     * The proxy class in this implementation does not have to be an interface.
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class, javax.inject.Provider)
     */
    public <T> T createProxy(Class<T> proxyClass, Provider<T> provider) throws GenerationException
    {
        FastClass fastClass = getProxyClass(getProxyClassLoader(proxyClass), proxyClass, new ProviderAdapter(provider));
        try {
            return proxyClass.cast(fastClass.newInstance());
        } catch(InvocationTargetException ite) {
            throw new GenerationException("Error creating Provider proxy", ite);
        } finally {
            clean(fastClass);
        }
    }

    /**
     * The proxy class in this implementation does not have to be an interface.
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class, org.proxy4j.core.ProxyHandler)
     */
    public <T> T createProxy(Class<T> proxyClass, final ProxyHandler<T> handler) throws GenerationException {
        FastClass fastClass = getProxyClass(getProxyClassLoader(proxyClass), proxyClass, new InvocationHandlerAdapter(handler));
        try {
            return proxyClass.cast(fastClass.newInstance());
        } catch(InvocationTargetException ite) {
            throw new GenerationException("Error creating Invocation proxy", ite);
        } finally {
            clean(fastClass);
        }
    }

    /**
     * The proxy classes must be interfaces, or an {@code GenerationException} will be thrown.
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class[], org.proxy4j.core.ProxyHandler)
     */
    public Object createProxy(Class<?>[] proxyInterfaces, final ProxyHandler<?> handler) throws GenerationException {
        if(proxyInterfaces.length==0)
            throw new IllegalArgumentException("Must be at least 1 proxy interface class");
        assertInterfaces(proxyInterfaces);
        FastClass fastClass = getProxyClass(getProxyClassLoader(proxyInterfaces[0]),
            proxyInterfaces, new InvocationHandlerAdapter(handler));
        try {
            return fastClass.newInstance();
        } catch(InvocationTargetException ite) {
            throw new GenerationException("Error creating Invocation proxy", ite);
        } finally {
            clean(fastClass);
        }
    }

    /**
     * The proxy class in this implementation does not have to be an interface.
     * @see org.proxy4j.core.ProxyFactory#buildInterceptor(Class) 
     */
    public <T> InterceptorBuilder<T> buildInterceptor(Class<T> proxyClass) {
        return new CglibInterceptorBuilder<T>(getProxyClassLoader(proxyClass));
    }

    //retrives or creates the FastClass for the proxy
    private FastClass getProxyClass(ClassLoader loader, Class<?> proxyClass, Callback callback) {
        FastClass fastClass = null; //TODO cache
        if(fastClass == null) {
            Class<?> enhanced = ClassGenerator.forType(loader, proxyClass)
                .generate(callback.getClass());
            fastClass = FastClass.create(enhanced);
        }
        Enhancer.registerCallbacks(fastClass.getJavaClass(), new Callback[]{callback});
        return fastClass;
    }

    //retrieves or creates the FastClass for the proxy
    private FastClass getProxyClass(ClassLoader loader, Class<?>[] interfaces, Callback callback) {
        FastClass fastClass = null; //TODO cache
        if(fastClass == null) {
           Class<?> enhanced = ClassGenerator.forTypes(loader, interfaces)
                .generate(callback.getClass());
           fastClass = FastClass.create(enhanced);
        }
        Enhancer.registerCallbacks(fastClass.getJavaClass(), new Callback[]{callback});
        return fastClass;
    }

    //cleans the enhanced class of any registered callbacks
    private void clean(FastClass fastClass) {
        Enhancer.registerCallbacks(fastClass.getJavaClass(), null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////    Utility classes.                                                                ////
    ////////////////////////////////////////////////////////////////////////////////////////////

    /** CGLIB adapter for a {@code Provider} */
    private static class ProviderAdapter implements Dispatcher
    {
        private Provider<?> provider;

        ProviderAdapter(Provider<?> provider) {
            this.provider = provider;
        }

        public Object loadObject() throws Exception {
            return provider.get();
        }
    }

    /** CGLIB adapter for a {@code ProxyInvocationHandler} */
    private static class InvocationHandlerAdapter implements net.sf.cglib.proxy.MethodInterceptor
    {
        private ProxyHandler<?> handler;

        InvocationHandlerAdapter(ProxyHandler<?> handler) {
            this.handler = handler;
        }

        @SuppressWarnings("unchecked")
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return handler.handle(new CglibProxyInvocation(proxy, method, methodProxy, args));
        }
    }
}
