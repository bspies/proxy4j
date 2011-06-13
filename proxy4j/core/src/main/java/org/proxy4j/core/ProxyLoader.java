package org.proxy4j.core;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifying annotation used to inject the proxy {@link ClassLoader}
 * into the .
 * @author Brennan Spies
 */
@Qualifier
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyLoader {
}
