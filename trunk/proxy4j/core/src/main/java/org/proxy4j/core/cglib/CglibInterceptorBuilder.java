package org.proxy4j.core.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.aopalliance.intercept.MethodInterceptor;
import org.proxy4j.core.GenerationException;
import org.proxy4j.core.InterceptorChain;
import org.proxy4j.core.InterceptorFactory;
import org.proxy4j.core.filter.AnnotationFilter;
import org.proxy4j.core.filter.MethodFilter;
import org.proxy4j.core.build.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>CGLIB implementation of {@link InterceptorBuilder} and related intermediate
 * builders.</p>
 * @author Brennan Spies
 */
class CglibInterceptorBuilder<T> implements InterceptorBuilder<T>
{
    private ClassLoader loader;
    private ProxyCreator<T> proxyCreator;
    private T target;

    CglibInterceptorBuilder(ClassLoader loader) {
       this.loader = loader;
    }

    @SuppressWarnings("unchecked")
    public InterceptorBindingBuilder<T> on(T target) {
        this.target = target;
        proxyCreator = new ProxyCreator<T>(loader, (Class<T>)target.getClass());
        return new CglibInterceptorBindingBuilder();
    }

    // Returns the appropriate {@code Callback}.
    private Callback getCallback(MethodInterceptor... interceptors) {
        //assume not 0
        return interceptors.length == 1 ? new InterceptorCallback(target, interceptors[0]) :
            new InterceptorChainCallback(target, interceptors);
    }

    // Returns the appropriate {@code Callback}.
    private Callback getCallback(List<MethodInterceptor> interceptors) {
        //assume not 0
        return interceptors.size()==1 ? new InterceptorCallback(target, interceptors.get(0)) :
            new InterceptorChainCallback(interceptors);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////   Intermediate builder implementations.                                            ////
    ////////////////////////////////////////////////////////////////////////////////////////////

    private abstract class AbstractMethodBinder implements MethodBinder<T>
    {
        private BindingCallbackMapper mapper;

        AbstractMethodBinder() {
            mapper = new BindingCallbackMapper();
        }

        AbstractMethodBinder(BindingCallbackMapper mapper) {
            this.mapper = mapper;
        }

        protected BindingCallbackMapper getMethodMapper() { return mapper; }

        // Maps the given method to the chain of interceptors
        protected void map(Method method, MethodInterceptor... interceptors) {
            mapper.bind(method, getCallback(interceptors));
        }

        protected void map(Method method, InterceptorFactory factory) {
            List<MethodInterceptor> interceptors = factory.getInterceptors(method);
            if (interceptors.size() == 0)
                throw new RuntimeException("InterceptorFactory must return non-empty list of interceptors");
            mapper.bind(method, getCallback(interceptors));
        }
    }

    /**
     * Initial builder for binding methods to interceptor callbacks.
     */
    private class CglibInterceptorBindingBuilder extends AbstractMethodBinder implements InterceptorBindingBuilder<T>
    {
        public InterceptorCreator<T> using(MethodFilter filter, MethodInterceptor... interceptors) {
            if(interceptors.length==0)
                throw new IllegalArgumentException("Must have at least 1 MethodInterceptor");
            for(Method m : getFilteredMethods(filter)) {
                map(m, interceptors);
            }
            return new CglibInterceptorCreator(getMethodMapper());
        }

        public InterceptorCreator<T> using(MethodFilter filter, InterceptorFactory factory) {
            for(Method m : getFilteredMethods(filter)) {
               map(m, factory);
            }
            return new CglibInterceptorCreator(getMethodMapper());
        }

        public InterceptorCreator<T> using(Class<? extends Annotation> methodMarker, MethodInterceptor... interceptors) {
            return using(AnnotationFilter.forAnnotation(methodMarker), interceptors);
        }

        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            map(method, interceptors);
            return new CglibMethodBindingBuilder(getMethodMapper());
        }

        private Collection<Method> getFilteredMethods(MethodFilter filter) {
            Set<Method> filteredMethods = new HashSet<Method>();
            for(Method m : proxyCreator.getProxyableMethods()) {
               if(filter.accept(m))
                   filteredMethods.add(m);
            }
            return filteredMethods;
        }
    }

    /**
     * Intermediate builder responsible for binding individual methods to interceptor callbacks.
     */
    private class CglibMethodBindingBuilder extends AbstractMethodBinder implements InterceptorCreator<T>, MethodBindingBuilder<T>
    {
       CglibMethodBindingBuilder(BindingCallbackMapper mapper) {
          super(mapper);
       }

        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            map(method, interceptors);
            return this;
        }

        public T create() throws GenerationException {
            InterceptorCreator<T> creator = new CglibInterceptorCreator(getMethodMapper());
            return creator.create();
        }
    }

    /**
     * The "last step" builder, from which the interceptor proxy can be made.
     */
    private class CglibInterceptorCreator implements InterceptorCreator<T>
    {
        private CallbackMapper mapper;

        CglibInterceptorCreator(CallbackMapper mapper) {
            this.mapper = mapper;
        }

        public T create() throws GenerationException {
            return proxyCreator.newProxy(mapper);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////    Utility classes                                                                 ////
    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maps proxied methods to {@link Callback} handlers.
     */
    private static class BindingCallbackMapper implements CallbackMapper
    {
        private Map<Method,Callback> map;

        public BindingCallbackMapper() {
            map = new HashMap<Method,Callback>();
        }

        void bind(Method m, Callback callback) {
            if(map.containsKey(m))
                throw new IllegalArgumentException("Method already bound to interceptor: " + m.getName());
            map.put(m, callback);
        }

        public Callback map(Method method) {
            Callback callback = map.get(method);
            return callback!=null ? callback : NoOp.INSTANCE;
        }
    }

    /** CGLIB adapter for invoking an {@code InterceptorChain}. */
    private static class InterceptorChainCallback implements net.sf.cglib.proxy.MethodInterceptor
    {
        private InterceptorChain chain;
        private Object target;

        InterceptorChainCallback(Object target, MethodInterceptor... interceptors) {
            this.target = target;
            chain = new InterceptorChain(interceptors);
        }

        InterceptorChainCallback(List<MethodInterceptor> interceptors) {
            chain = new InterceptorChain(interceptors);
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return chain.invoke(new CglibMethodInvocation(target, method, methodProxy, args));
        }
    }

    /** CGLIB adapter for invoking a {@link MethodInterceptor}. */
    private static class InterceptorCallback implements net.sf.cglib.proxy.MethodInterceptor
    {
        private MethodInterceptor interceptor;
        private Object target;

        InterceptorCallback(Object target, MethodInterceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return interceptor.invoke(new CglibMethodInvocation(target, method, methodProxy, args));
        }
    }
}
