package org.proxy4j.remote;

import java.lang.annotation.*;

/**
 * @author Brennan Spies
 * <p>Marker annotation for classes/interfaces whose public methods are remotely
 * exportable.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Exportable {
}
