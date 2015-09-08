package org.proxy4j.remote;

import java.lang.annotation.*;

/**
 * Marker annotation for classes/interfaces whose public methods are remotely
 * exportable.
 *
 * @author Brennan Spies
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Exportable {
}
