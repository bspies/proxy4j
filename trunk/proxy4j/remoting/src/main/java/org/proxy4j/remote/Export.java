package org.proxy4j.remote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking individual methods as exported (or not). Must
 * be used on classes marked {@link Exportable}. By default, this is true
 * for public methods of the class where this annotation is absent. This
 * annotation is be ignored for non-public methods.
 *
 * @author Brennan Spies
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Export {
    /**
     * Flag to indicate whether or not the annotated method is
     * remotely exported.
     * @return True if method exported
     */
    public boolean value() default true;
}
