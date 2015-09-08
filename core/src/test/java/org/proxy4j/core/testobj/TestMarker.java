package org.proxy4j.core.testobj;

import java.lang.annotation.*;

/**
 * <p>Test annotation for method interception.</p>
 * @author Brennan Spies
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface TestMarker {
}
