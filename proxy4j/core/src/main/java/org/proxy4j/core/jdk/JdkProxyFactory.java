package org.proxy4j.core.jdk;

import org.proxy4j.core.BaseProxyFactory;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.ProxyHandler;
import org.proxy4j.core.build.InterceptorBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p></p>
 * @author Brennan Spies
 * @since 1.0
 */
public class JdkProxyFactory extends BaseProxyFactory
{
    public JdkProxyFactory() {
        super();
    }

    @Inject
    public JdkProxyFactory(ClassLoader loader) {
        super(loader);
    }

    /**
     * The proxy class (first argument) must be an interface, or an {@code IllegalArgumentException} will be thrown.
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class, javax.inject.Provider)
     */
    public <T> T createProxy(Class<T> proxyInterface, final Provider<T> provider) throws GenerationException {
        return proxyInterface.cast(Proxy.newProxyInstance(getProxyClassLoader(proxyInterface),
            new Class[]{ proxyInterface },
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return method.invoke(provider.get(), args);
                }
            })
        );
    }

    /**
     * The proxy class (first argument) must be an interface, or an {@code IllegalArgumentException} will be thrown.
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class, org.proxy4j.core.ProxyHandler)
     */
    public <T> T createProxy(Class<T> proxyInterface, final ProxyHandler<T> handler) throws GenerationException {
        return proxyInterface.cast(Proxy.newProxyInstance(getProxyClassLoader(proxyInterface),
            new Class[]{proxyInterface},
            new InvocationHandler() {
                @SuppressWarnings("unchecked")
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return handler.handle(new JdkProxyInvocation(proxy, method, args)); 
                }
            })
        );
    }

    /**
     * @see org.proxy4j.core.ProxyFactory#createProxy(Class[], org.proxy4j.core.ProxyHandler)
     */
    public Object createProxy(Class<?>[] proxyInterfaces, final ProxyHandler<?> handler) throws GenerationException {
        if(proxyInterfaces.length==0)
            throw new IllegalArgumentException("Must define at least 1 proxy interface");
        return Proxy.newProxyInstance(getProxyClassLoader(proxyInterfaces[0]),
            proxyInterfaces,
            new InvocationHandler() {
                @SuppressWarnings("unchecked")
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return handler.handle(new JdkProxyInvocation(proxy, method, args));
                }
            }
        );
    }

    /**
     * @see org.proxy4j.core.ProxyFactory#buildInterceptor(Class) 
     */
    public <T> InterceptorBuilder<T> buildInterceptor(Class<T> proxyClass) {
        return new JdkInterceptorBuilder<T>(getProxyClassLoader(proxyClass), proxyClass);
    }
}