package org.proxy4j.core.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>An implementation of {@link MethodFilter} that selects methods
 * marked with a given annotation.</p>
 * @author Brennan Spies
 */
public class AnnotationFilter<T extends Annotation> implements MethodFilter
{
    private Class<T> annotation;

    private AnnotationFilter(Class<T> annotation) {
        this.annotation = annotation;
    }

    public static <A extends Annotation> AnnotationFilter<A> forAnnotation(Class<A> annotation) {
        return new AnnotationFilter<A>(annotation);
    }

    /**
     * Accepts the method if it has an annotation of type {@code T}.
     * @param method The method to test
     * @return True if method has annotation
     */
    public boolean accept(Method method) {
        return method.isAnnotationPresent(annotation);
    }
}
