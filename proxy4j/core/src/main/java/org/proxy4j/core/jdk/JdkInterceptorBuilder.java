package org.proxy4j.core.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.InterceptorChain;
import org.proxy4j.core.InterceptorFactory;
import org.proxy4j.core.build.InterceptorBindingBuilder;
import org.proxy4j.core.build.InterceptorBuilder;
import org.proxy4j.core.build.InterceptorCreator;
import org.proxy4j.core.build.MethodBindingBuilder;
import org.proxy4j.core.filter.AnnotationFilter;
import org.proxy4j.core.filter.MethodFilter;
import org.proxy4j.core.reflect.InheritableMethodExtractor;
import org.proxy4j.core.reflect.SignatureKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Implementation of {@link InterceptorBuilder} using JDK dynamic proxies. This implementation
 * has important limitation in respect to other implementations in that it is restricted to
 * proxying interfaces.</p>
 * @author Brennan Spies
 */
class JdkInterceptorBuilder<T> implements InterceptorBuilder<T> {

    private ClassLoader loader;
    private Class<T> interfaceClass;
    private T target;

    JdkInterceptorBuilder(ClassLoader classLoader, Class<T> interfaceClass) {
        this.loader = classLoader;
        this.interfaceClass = interfaceClass;
    }

    /** {@inheritDoc} */
    public InterceptorBindingBuilder<T> on(T target) {
        this.target = target;
        return new JdkInterceptorBindingBuilder();
    }

    private static class AbstractBindingBuilder
    {
       private Map<SignatureKey,InterceptorChain> methodMap;

       AbstractBindingBuilder() {
          methodMap = new LinkedHashMap<SignatureKey,InterceptorChain>();
       }

       AbstractBindingBuilder(Map<SignatureKey, InterceptorChain> methodMap) {
          this.methodMap = methodMap;
       }

       void bind(Method method, InterceptorChain chain) {
           SignatureKey key = new SignatureKey(method);
           if(methodMap.containsKey(key))
              throw new IllegalArgumentException("Method already bound to interceptor: " + method.getName());
           methodMap.put(key, chain);
       }

       protected Map<SignatureKey,InterceptorChain> getMethodMap() { return methodMap; }
    }

    /**
     * JDK implementation of {@link InterceptorCreator}.
     */
    private class JdkInterceptorCreator extends AbstractBindingBuilder implements InterceptorCreator<T>
    {
        private Map<SignatureKey,InterceptorChain> methodMap;

        JdkInterceptorCreator(Map<SignatureKey,InterceptorChain> methodMap) {
            this.methodMap = methodMap;
        }

        /** {@inheritDoc} */
        public T create() throws GenerationException {
            return interfaceClass.cast(Proxy.newProxyInstance(loader,
                    new Class<?>[] {interfaceClass},
                    new InterceptorInvocationHandler(target, methodMap)));
        }
    }

    /**
     * JDK implementation of {@link MethodBindingBuilder}.
     */
    private class JdkMethodBindingBuilder extends AbstractBindingBuilder implements MethodBindingBuilder<T>
    {
        JdkMethodBindingBuilder(Map<SignatureKey,InterceptorChain> methodMap) {
            super(methodMap);
        }

        /** {@inheritDoc} */
        public T create() throws GenerationException {
            return new JdkInterceptorCreator(getMethodMap()).create();
        }

        /** {@inheritDoc} */
        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            bind(method, new InterceptorChain(interceptors));
            return this;
        }
    }

    /**
     * JDK implementation of {@link InterceptorBindingBuilder}.
     */
    private class JdkInterceptorBindingBuilder extends AbstractBindingBuilder implements InterceptorBindingBuilder<T>
    {
        /** {@inheritDoc} */
        public InterceptorCreator<T> using(MethodFilter filter, MethodInterceptor... interceptors) {
            InterceptorChain chain = new InterceptorChain(interceptors);
            for(Method m : interfaceClass.getMethods()) {
               bind(m, chain);
            }
            return new JdkInterceptorCreator(getMethodMap());
        }

        /** {@inheritDoc} */
        public InterceptorCreator<T> using(MethodFilter filter, InterceptorFactory factory) {
            for(Method m : interfaceClass.getMethods()) {
               bind(m, new InterceptorChain(factory.getInterceptors(m))); 
            }
            return new JdkInterceptorCreator(getMethodMap());
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public InterceptorCreator<T> using(Class<? extends Annotation> methodMarker, MethodInterceptor... interceptors) {
            InterceptorChain chain = new InterceptorChain(interceptors);
            //TODO move this to class var?
            for(Method m : new InheritableMethodExtractor((Class<T>)target.getClass(),interfaceClass)
                    .getMethods(AnnotationFilter.forAnnotation(methodMarker))) {
                bind(m, chain);
            }
            return new JdkInterceptorCreator(getMethodMap());
        }

        /** {@inheritDoc} */
        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            bind(method, new InterceptorChain(interceptors));
            return new JdkMethodBindingBuilder(getMethodMap());
        }
    }

    /**
     * Implementation of {@link InvocationHandler} that invokes the interceptor chain
     * on methods that are intercepted.
     */
    private static class InterceptorInvocationHandler implements InvocationHandler
    {
        private Map<SignatureKey,InterceptorChain> interceptors;
        private Object target;

        InterceptorInvocationHandler(Object target, Map<SignatureKey,InterceptorChain> interceptors) {
          this.target = target;
          this.interceptors = interceptors; //TODO copy?
        }

        /**
         * Invokes the interceptor chain if the given method has one. Otherwise passes the
         * call through directly to the target.
         *
         * {@inheritDoc}
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            InterceptorChain chain = interceptors.get(new SignatureKey(method));
            if(chain==null) {
               return method.invoke(target, args);
            } else {
               return chain.invoke(new JdkMethodInvocation(target, method, args));
            }
        }
    }
}
