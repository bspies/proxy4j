package org.proxy4j.core.javassist;

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
import org.proxy4j.core.reflect.BasicMethodExtractor;
import org.proxy4j.core.reflect.MethodExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Javassist implementation of {@link InterceptorBuilder}.</p>
 * @author Brennan Spies
 */
public class JavassistInterceptorBuilder<T> implements InterceptorBuilder<T>
{
    private final ClassLoader loader;
    private MethodExtractor extractor;
    private final ClassGenerator generator;
    private T target;

    JavassistInterceptorBuilder(ClassLoader loader, Class<T> proxyInterface, ClassGenerator generator) {
        this.loader = loader;
        this.generator = generator;
        //this.extractor = new BasicMethodExtractor(proxyInterface);
    }

    public InterceptorBindingBuilder<T> on(T target) {
        this.target = target;
        this.extractor = new BasicMethodExtractor(target.getClass());
        return new JavassistInterceptorBindingBuilder();
    }

    private static class AbstractBindingBuilder
    {
       private final Map<Method,InterceptorChain> methodMap;

       AbstractBindingBuilder() {
          methodMap = new LinkedHashMap<Method,InterceptorChain>();
       }

       AbstractBindingBuilder(Map<Method, InterceptorChain> methodMap) {
          this.methodMap = methodMap;
       }

       void bind(Method method, InterceptorChain chain) {
           if(methodMap.containsKey(method))
              throw new IllegalArgumentException("Method already bound to interceptor: " + method.getName());
           methodMap.put(method, chain);
       }

       protected Map<Method, InterceptorChain> getMethodMap() { return methodMap; }
    }

    /* Intermediate interceptor builder */
    private class JavassistInterceptorBindingBuilder extends AbstractBindingBuilder implements InterceptorBindingBuilder<T>
    {
        public InterceptorCreator<T> using(MethodFilter filter, MethodInterceptor... interceptors) {
            for(Method m : extractor.getMethods(filter)) {
                bind(m, new InterceptorChain(interceptors));
            }
            return new JavassistInterceptorCreator(getMethodMap());
        }

        public InterceptorCreator<T> using(MethodFilter filter, InterceptorFactory factory) {
            for(Method m : extractor.getMethods(filter)) {
                bind(m, new InterceptorChain(factory.getInterceptors(m)));
            }
            return new JavassistInterceptorCreator(getMethodMap());
        }

        public InterceptorCreator<T> using(Class<? extends Annotation> methodMarker, MethodInterceptor... interceptors) {
            for(Method m : extractor.getMethods(AnnotationFilter.forAnnotation(methodMarker))) {
                bind(m, new InterceptorChain(interceptors));
            }
            return new JavassistInterceptorCreator(getMethodMap());
        }

        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            bind(method, new InterceptorChain(interceptors));
            return new JavassistMethodBindingBuilder(getMethodMap());
        }
    }

    /* Intermediate builder for individual methods */
    private class JavassistMethodBindingBuilder extends AbstractBindingBuilder implements MethodBindingBuilder<T>
    {
        JavassistMethodBindingBuilder(Map<Method, InterceptorChain> methodMap) {
           super(methodMap);
        }

        public MethodBindingBuilder<T> using(Method method, MethodInterceptor... interceptors) {
            bind(method, new InterceptorChain(interceptors));
            return this;
        }

        public T create() throws GenerationException {
            return new JavassistInterceptorCreator(getMethodMap()).create();
        }
    }

    /* Final step builder */
    private class JavassistInterceptorCreator implements InterceptorCreator<T>
    {
        private Map<Method,InterceptorChain> methodMap;

        JavassistInterceptorCreator(Map<Method,InterceptorChain> methodMap) {
            this.methodMap = methodMap;
        }

        public T create() throws GenerationException {
            try {
                Class<T> proxyClass = generator.getInterceptorProxyClass(loader, target, methodMap);
                InterceptorChain[] chains = methodMap.values().toArray(new InterceptorChain[methodMap.size()]);
                return proxyClass.getConstructor(target.getClass(), chains.getClass()).newInstance(target, chains);
            } catch (Exception e) {
                throw new GenerationException("Unable to generator interceptor proxy", e);
            }  
        }
    }
}
